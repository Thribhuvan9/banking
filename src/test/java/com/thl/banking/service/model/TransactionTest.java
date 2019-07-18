package com.thl.banking.service.model;

import com.thl.banking.model.Account;
import com.thl.banking.model.Transaction;
import com.thl.banking.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TransactionTest {

    @Test
    public void shouldCheckEquals() {
        Account sender = createAccount();
        Account receiver = createAccount();
        Transaction transaction = createTransaction(sender, receiver, new BigDecimal(1000), "INR");
        Transaction transaction2 = createTransaction(sender, receiver, new BigDecimal(1000), "INR");
        assertThat(transaction.equals(transaction2)).isFalse();
        assertThat(transaction == transaction2).isFalse();
        transaction2.setId(transaction.getId());
        transaction2.setCreatedTime(transaction.getCreatedTime());
        assertThat(transaction.equals(transaction2)).isTrue();
        assertThat(transaction == transaction2).isFalse();
        assertThat(transaction == transaction).isTrue();
        assertThat(transaction.hashCode() == transaction.hashCode()).isTrue();

    }


    private Account createAccount() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setAddress("");
        user.setName("TestUser");
        Account account = new Account(user,new BigDecimal(10000), LocalDateTime.now().toString(),LocalDateTime.now().toString(),"INR");
        account.setAccountId(UUID.randomUUID().toString());
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
