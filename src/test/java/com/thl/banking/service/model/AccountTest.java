package com.thl.banking.service.model;

import com.thl.banking.model.Account;
import com.thl.banking.model.User;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AccountTest {

    @Test
    public void shouldCheckEquals() {
        Account account = createAccount();
        Account account2 = createAccount();
        assertThat(account.equals(account)).isTrue();
        assertThat(account.hashCode() != account2.hashCode()).isTrue();
        account2.setAccountId(account.getAccountId());
        account2.setUser(account.getUser());
        account2.setCreatedTime(account.getCreatedTime());
        account2.setUpdatedTime(account.getUpdatedTime());
        assertThat(account.equals(account2)).isTrue();
        assertThat(account == account2).isFalse();

    }


    private Account createAccount() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setAddress("");
        user.setName("TestUser");
        Account account = new Account(user,new BigDecimal(10000), LocalDateTime.now(),LocalDateTime.now(),"INR");
        account.setAccountId(UUID.randomUUID().toString());
        return account;
    }

}
