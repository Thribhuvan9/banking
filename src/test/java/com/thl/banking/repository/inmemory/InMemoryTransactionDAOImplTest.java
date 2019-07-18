package com.thl.banking.repository.inmemory;

import com.thl.banking.exception.InsufficientFundException;
import com.thl.banking.model.Account;
import com.thl.banking.model.Transaction;
import com.thl.banking.model.User;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.repository.TransactionDAO;
import com.thl.banking.validator.TransactionValidation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryTransactionDAOImplTest{

    private TransactionDAO transactionDAO;

    @Mock
    private AccountDAO accountDAO;

    @Mock
    private TransactionValidation transactionValidation;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        transactionDAO = new InMemoryTransactionDAOImpl(accountDAO, transactionValidation);
    }


    @Test
    public void transactionWhenValidationDetectedErrorTest() throws Exception {
        Account sender = createAccount(new BigDecimal(10000));
        Account receiver = createAccount(new BigDecimal(5000));
        Transaction transaction = createTransaction(sender, receiver, new BigDecimal(1000), "INR");
        when(transactionValidation.validate(transaction)).thenReturn(new InsufficientFundException(sender.getAccountId()));
        expectedException.expect(InsufficientFundException.class);
        transactionDAO.commit(transaction);
    }

    @Test
    public void commitTransactionTest() throws Exception {
        Account sender = createAccount(new BigDecimal(10000));
        Account receiver = createAccount(new BigDecimal(5000));
        Transaction transaction = createTransaction(sender, receiver, new BigDecimal(1000), "INR");
        when(transactionValidation.validate(transaction)).thenReturn(null);
        when(accountDAO.getAccountByAccountNumber(sender.getAccountId())).thenReturn(Optional.of(sender));
        when(accountDAO.getAccountByAccountNumber(receiver.getAccountId())).thenReturn(Optional.of(receiver));
        transactionDAO.commit(transaction);
        verify(accountDAO).withdrawMoney(sender, transaction.amount(), "INR");
        verify(accountDAO).depositMoney(receiver, transaction.amount(), "INR");
        assertThat(transactionDAO.getAllTransactions().isEmpty()).isFalse();
    }

    @Test
    public void createdTransactionTest() throws Exception {
        Account sender = createAccount(new BigDecimal(10000));
        Account receiver = createAccount(new BigDecimal(5000));
        Transaction transaction = createTransaction(sender, receiver, new BigDecimal(1000), "INR");
        when(transactionValidation.validate(transaction)).thenReturn(null);
        when(accountDAO.getAccountByAccountNumber(sender.getAccountId())).thenReturn(Optional.of(sender));
        when(accountDAO.getAccountByAccountNumber(receiver.getAccountId())).thenReturn(Optional.of(receiver));
        transactionDAO.commit(transaction);
        Transaction createdTransaction = transactionDAO.getTransactionById(transaction.getId()).get();
        assertThat(createdTransaction.equals(transaction)).isTrue();
        assertThat(createdTransaction.getId()).isEqualTo(transaction.getId());
        assertThat(createdTransaction.from()).isEqualTo(transaction.from());
        assertThat(createdTransaction.to()).isEqualTo(transaction.to());
        assertThat(createdTransaction.amount()).isEqualTo(transaction.amount());
    }

    @Test
    public void getAllTransactionsTest() throws Exception {
        Account sender = createAccount(new BigDecimal(10000));
        Account receiver = createAccount(new BigDecimal(5000));
        Transaction transaction = createTransaction(sender, receiver, new BigDecimal(1000), "INR");
        Transaction transaction2 = createTransaction(sender, receiver, new BigDecimal(2000), "INR");
        when(transactionValidation.validate(transaction)).thenReturn(null);
        when(transactionValidation.validate(transaction2)).thenReturn(null);
        when(accountDAO.getAccountByAccountNumber(sender.getAccountId())).thenReturn(Optional.of(sender));
        when(accountDAO.getAccountByAccountNumber(receiver.getAccountId())).thenReturn(Optional.of(receiver));
        transactionDAO.commit(transaction);
        transactionDAO.commit(transaction2);
        assertThat(transactionDAO.getAllTransactions().size()).isEqualTo(2);
    }

    @Test
    public void getErrorWhenSenderBalanceIsLessThanMoneyToBeSendTest() throws Exception {
        Account sender = createAccount(new BigDecimal(10000));
        Account receiver = createAccount(new BigDecimal(5000));
        Transaction transaction = createTransaction(sender, receiver, new BigDecimal(11000), "INR");
        when(transactionValidation.validate(transaction)).thenReturn(null);
        when(accountDAO.getAccountByAccountNumber(sender.getAccountId())).thenReturn(Optional.of(sender));
        when(accountDAO.getAccountByAccountNumber(receiver.getAccountId())).thenReturn(Optional.of(receiver));
        expectedException.expect(InsufficientFundException.class);
        transactionDAO.commit(transaction);

    }

    @Test
    public void clearTransactionsTest() throws Exception {
        Account sender = createAccount(new BigDecimal(10000));
        Account receiver = createAccount(new BigDecimal(5000));
        Transaction transaction = createTransaction(sender, receiver, new BigDecimal(1000), "INR");
        Transaction transaction2 = createTransaction(sender, receiver, new BigDecimal(2000), "INR");
        when(transactionValidation.validate(transaction)).thenReturn(null);
        when(transactionValidation.validate(transaction2)).thenReturn(null);
        when(accountDAO.getAccountByAccountNumber(sender.getAccountId())).thenReturn(Optional.of(sender));
        when(accountDAO.getAccountByAccountNumber(receiver.getAccountId())).thenReturn(Optional.of(receiver));
        transactionDAO.commit(transaction);
        transactionDAO.commit(transaction2);
        assertThat(transactionDAO.getAllTransactions().size()).isEqualTo(2);
        transactionDAO.clear();
        assertThat(transactionDAO.getAllTransactions().isEmpty()).isTrue();
    }

    private Account createAccount(BigDecimal balance) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setAddress("");
        user.setName("TestUser");
        Account account = new Account();
        account.setUser(user);
        account.setAccountId(UUID.randomUUID().toString());
        account.setBalance(balance);
        account.setCurrencyUnit("INR");
        account.setUpdatedTime(LocalDateTime.now().toString());
        account.setCreatedTime(LocalDateTime.now().toString());
        return account;
    }

    private Transaction createTransaction(Account from, Account to, BigDecimal amount, String currencyUnit) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setAmount(amount);
        transaction.setCurrencyUnit(currencyUnit);
        transaction.setCreatedTime(LocalDateTime.now().toString());
        return transaction;
    }
}
