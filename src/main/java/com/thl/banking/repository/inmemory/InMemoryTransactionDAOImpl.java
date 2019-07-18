package com.thl.banking.repository.inmemory;

import com.thl.banking.exception.InsufficientFundException;
import com.thl.banking.model.Account;
import com.thl.banking.model.Transaction;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.repository.TransactionDAO;
import com.thl.banking.validator.TransactionValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class InMemoryTransactionDAOImpl implements TransactionDAO {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final long FIXED_DELAY = 3;
    private static final long TIMEOUT = TimeUnit.SECONDS.toNanos(3);

    private final Queue<Transaction> transactions = new LinkedList<>();
    private AccountDAO accountDAO;
    private TransactionValidation transactionValidation;
    Object tLock = new Object();

    @Inject
    public InMemoryTransactionDAOImpl(AccountDAO accountDAO, TransactionValidation transactionValidation) {
        this.accountDAO = accountDAO;
        this.transactionValidation = transactionValidation;
    }

    @Override
    public Optional<Transaction> getTransactionById(String id) {
        return transactions.stream().filter(transaction -> transaction.getId().equals(id)).findFirst();
    }

    @Override
    public Queue<Transaction> getAllTransactions() {
        return transactions;
    }

    @Override
    public Transaction commit(Transaction transaction) throws Exception {
        {
            Account from;
            Account to;

            long stopTime = System.nanoTime() + TIMEOUT;

            while (transaction.isRunning().get()) {
                synchronized (tLock) {
                    Exception error = transactionValidation.validate(transaction);
                    if (error != null) {
                        throw error;
                    }

                    from = accountDAO.getAccountByAccountNumber(transaction.from().getAccountId()).get();
                    to = accountDAO.getAccountByAccountNumber(transaction.to().getAccountId()).get();

                    if (from.getBalance().compareTo(transaction.amount()) < 0) {
                        throw new InsufficientFundException(from.getAccountId());
                    }
                }

                BigDecimal senderBalance = from.getBalance();
                BigDecimal receiverBalance = to.getBalance();

                if (from.lock().tryLock()) {
                    try {
                        if (to.lock().tryLock()) {
                            try {
                                accountDAO.withdrawMoney(from, transaction.amount(), transaction.getCurrencyUnit());
                                accountDAO.depositMoney(to, transaction.amount(), transaction.getCurrencyUnit());
                                transaction.isRunning().set(false);
                                transaction.setStatus("TRANSACTION SUCCESSFUL");
                            } catch (Exception e) {
                                logger.error("Transaction Failed, rollback initiated");
                                transaction.setStatus("TRANSACTION FAILED:ROLLED_BACK");
                                from.setBalance(senderBalance);
                                to.setBalance(receiverBalance);
                                accountDAO.updateByAccountNumber(transaction.from().getAccountId(), from);
                                accountDAO.updateByAccountNumber(transaction.to().getAccountId(), to);
                                throw e;
                            } finally {
                                transactions.add(transaction);
                                to.lock().unlock();
                            }
                        }
                    } finally {
                        from.lock().unlock();
                    }
                }

                if (System.nanoTime() > stopTime) {
                    transaction.isRunning().set(false);
                }

                try {
                    TimeUnit.NANOSECONDS.sleep(FIXED_DELAY + (new Random().nextInt() % TIMEOUT));
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    transaction.setStatus("TRANSACTION INTERRUPTED");
                    throw new RuntimeException(exception);
                }
            }

            return transaction;
        }
    }

    @Override
    public void clear() {
        transactions.clear();
    }
}
