package com.thl.banking.validator.impl;

import com.thl.banking.exception.CurrentcyMismatchException;
import com.thl.banking.exception.InvalidAccountException;
import com.thl.banking.exception.TransferBetweenSameAccountException;
import com.thl.banking.model.Account;
import com.thl.banking.model.Transaction;
import com.thl.banking.model.User;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.validator.TransactionValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionValidationImplTest {

    private TransactionValidation transactionValidation;

    @Mock
    private AccountDAO accountDAO;

    @Before
    public void setUp() {
        transactionValidation = new TransactionValidationImpl(accountDAO);
    }

    @Test
    public void getCommitErrorForValidTransaction() {
        Account sender = createAccount(100, "testSenderName");
        Account receiver = createAccount(10, "testReceiverName");

        when(accountDAO.getAccountByAccountNumber(sender.getAccountId())).thenReturn(Optional.of(sender));
        when(accountDAO.getAccountByAccountNumber(receiver.getAccountId())).thenReturn(Optional.of(receiver));

        Transaction transaction = new Transaction(sender, receiver, new BigDecimal(10), "INR");
        Exception error = transactionValidation.validate(transaction);

        assertThat(error).isNull();
    }

    @Test
    public void getErrorWhenSenderAccountDoesNotExist() {
        Account receiver = createAccount(10, "testReceiverName");
        Account sender = createAccount(100, "testSenderName");
        when(accountDAO.getAccountByAccountNumber(sender.getAccountId())).thenReturn(Optional.empty());

        Transaction transaction = new Transaction(sender, receiver, new BigDecimal(10), "INR");
        Exception error = transactionValidation.validate(transaction);

        assertThat(error).isNotNull();
        assertThat(error).isInstanceOf(InvalidAccountException.class);
        assertThat(error.getMessage()).isEqualTo("INVALID_ACCOUNT_EXCEPTION:ACCOUNT_DOESNT_EXIST");

    }

    @Test
    public void getErrorWhenReceiverAccountDoesNotExist() {
        Account receiver = createAccount(10, "testReceiverName");
        Account sender = createAccount(100, "testSenderName");
        when(accountDAO.getAccountByAccountNumber(receiver.getAccountId())).thenReturn(Optional.empty());
        when(accountDAO.getAccountByAccountNumber(sender.getAccountId())).thenReturn(Optional.of(receiver));

        Transaction transaction = new Transaction(sender, receiver, new BigDecimal(10), "INR");
        Exception error = transactionValidation.validate(transaction);

        assertThat(error).isNotNull();
        assertThat(error).isInstanceOf(InvalidAccountException.class);
        assertThat(error.getMessage()).isEqualTo("INVALID_ACCOUNT_EXCEPTION:ACCOUNT_DOESNT_EXIST");
    }


    @Test
    public void getErrorWhenTwoAccountsHaveFundsInDifferentCurrencies() {
        Account sender = createAccount(100, "testSenderName");
        Account receiver = createAccount(10, "testReceiverName");
        receiver.setCurrencyUnit("USD");

        when(accountDAO.getAccountByAccountNumber(sender.getAccountId())).thenReturn(Optional.of(sender));
        when(accountDAO.getAccountByAccountNumber(receiver.getAccountId())).thenReturn(Optional.of(receiver));

        Transaction transaction = new Transaction(sender, receiver, new BigDecimal(10), "INR");
        Exception error = transactionValidation.validate(transaction);
        assertThat(error).isNotNull();
        assertThat(error).isInstanceOf(CurrentcyMismatchException.class);
    }

    @Test
    public void getErrorWhenSenderAndReceiverHasTheSameAccountNumber() {
        Account sender = createAccount(100, "testSenderName");
        Account receiver = sender;
        receiver.setCurrencyUnit("USD");

        when(accountDAO.getAccountByAccountNumber(sender.getAccountId())).thenReturn(Optional.of(sender));
        when(accountDAO.getAccountByAccountNumber(receiver.getAccountId())).thenReturn(Optional.of(receiver));

        Transaction transaction = new Transaction(sender, receiver, new BigDecimal(10), "INR");
        Exception error = transactionValidation.validate(transaction);
        assertThat(error).isNotNull();
        assertThat(error).isInstanceOf(TransferBetweenSameAccountException.class);
    }

    private Account createAccount(Integer amount, String usename) {
        User user = new User(UUID.randomUUID().toString(), usename, "");
        Account account = new Account();
        account.setUser(user);
        account.setAccountId(UUID.randomUUID().toString());
        account.setBalance(new BigDecimal(amount));
        account.setCurrencyUnit("INR");
        return account;
    }

}
