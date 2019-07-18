package com.thl.banking.repository.inmemory;

import com.thl.banking.model.Account;
import com.thl.banking.model.Transaction;
import com.thl.banking.model.User;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.repository.TransactionDAO;
import com.thl.banking.validator.TransactionValidation;
import com.thl.banking.validator.impl.AccountValidationImpl;
import com.thl.banking.validator.impl.TransactionValidationImpl;
import net.jodah.concurrentunit.Waiter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class ConcurrentTransactionTest {
    private static final int NUMBER_OF_THREADS = 10;

    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;
    private ExecutorService executorService;
    private Waiter waiter;

    @Before
    public void setUp() {
        accountDAO = new InMemoryAccountDAOImpl(new AccountValidationImpl());
        TransactionValidation transactionValidation = new TransactionValidationImpl(accountDAO);
        transactionDAO = new InMemoryTransactionDAOImpl(accountDAO, transactionValidation);
        waiter = new Waiter();
        executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }

    @After
    public void destroy() {
        executorService.shutdown();
    }

    @Test
    public void handleConcurrentTransactionsTest() throws Exception {
        final Account sender = createAccount(new BigDecimal(10000), "sender");
        final Account receiver = createAccount(new BigDecimal(5000), "receiver");
        accountDAO.create(sender);
        accountDAO.create(receiver);

        Transaction transaction1 = createTransaction(sender, receiver, new BigDecimal(100), "INR");
        Transaction transaction2 = createTransaction(sender, receiver, new BigDecimal(300), "INR");
        Transaction transaction3 = createTransaction(receiver, sender, new BigDecimal(80), "INR");

        executorService.submit(() -> commitTransaction(transaction1));
        executorService.submit(() -> commitTransaction(transaction2));
        executorService.submit(() -> commitTransaction(transaction3));
        waiter.await(5, TimeUnit.SECONDS, 3);

        BigDecimal senderMoney = accountDAO.getAccountByAccountNumber(sender.getAccountId()).get().getBalance();
        BigDecimal receiverMoney = accountDAO.getAccountByAccountNumber(receiver.getAccountId()).get().getBalance();

        assertThat(transactionDAO.getAllTransactions().size()).isEqualTo(3);
        assertThat(senderMoney).isEqualTo(new BigDecimal(9680));
        assertThat(receiverMoney).isEqualTo(new BigDecimal(5320));
    }

    @Test
    public void transactionTest() throws Exception {
        final Account sender = createAccount(new BigDecimal(100), "sender");
        final Account receiver = createAccount(new BigDecimal(0), "receiver");
        accountDAO.create(sender);
        accountDAO.create(receiver);

        Transaction transaction1 = createTransaction(sender, receiver, new BigDecimal(100), "INR");
        Transaction transaction2 = createTransaction(sender, receiver, new BigDecimal(100), "INR");
        executorService.submit(() -> commitTransaction(transaction1));
        executorService.submit(() -> commitTransaction(transaction2));

        waiter.await(5, TimeUnit.SECONDS, 1);

        BigDecimal senderMoney = accountDAO.getAccountByAccountNumber(sender.getAccountId()).get().getBalance();
        BigDecimal receiverMoney = accountDAO.getAccountByAccountNumber(receiver.getAccountId()).get().getBalance();

        assertThat(transactionDAO.getAllTransactions().size()).isEqualTo(1);
        assertThat(senderMoney).isEqualTo(new BigDecimal(0));
        assertThat(receiverMoney).isEqualTo(new BigDecimal(100));
    }

    @Test
    public void largeTransactionsTest() throws Exception {
        final Account sender = createAccount(new BigDecimal(10000), "sender");
        final Account receiver = createAccount(new BigDecimal(5000), "receiver");
        accountDAO.create(sender);
        accountDAO.create(receiver);

        for (int i = 0; i < 25; i++) {
            executorService.submit(() -> commitTransaction(createTransaction(sender, receiver, new BigDecimal(100), "INR")));
            executorService.submit(() -> commitTransaction(createTransaction(receiver,sender, new BigDecimal(40), "INR")));
        }
        waiter.await(2, TimeUnit.SECONDS, 3);
        executorService.shutdown();
        executorService.awaitTermination(1,TimeUnit.HOURS);
        BigDecimal senderMoney = accountDAO.getAccountByAccountNumber(sender.getAccountId()).get().getBalance();
        BigDecimal receiverMoney = accountDAO.getAccountByAccountNumber(receiver.getAccountId()).get().getBalance();

        assertThat(transactionDAO.getAllTransactions().size()).isEqualTo(50);
        assertThat(senderMoney).isEqualTo(new BigDecimal(8500));
        assertThat(receiverMoney).isEqualTo(new BigDecimal(6500));
    }

    private void commitTransaction(Transaction transaction) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(3000));
            transactionDAO.commit(transaction);
            waiter.assertNotNull(transaction);
            System.out.println(String.format("executing: %s, time: %d ms, thread: %s",
                    transaction.getId(),
                    TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS),
                    Thread.currentThread().getName())
            );
            waiter.resume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Account createAccount(BigDecimal balance, String type) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setAddress("");
        user.setName(type + "TestUser");
        Account account = new Account();
        account.setUser(user);
        account.setAccountId(UUID.randomUUID().toString());
        account.setBalance(balance);
        account.setCurrencyUnit("INR");
        account.setUpdatedTime(LocalDateTime.now());
        account.setCreatedTime(LocalDateTime.now());
        return account;
    }

    private Transaction createTransaction(Account from, Account to, BigDecimal amount, String currencyUnit) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setAmount(amount);
        transaction.setCurrencyUnit(currencyUnit);
        transaction.setCreatedTime(LocalDateTime.now());
        return transaction;
    }

}
