package com.thl.banking.validator.impl;

import com.thl.banking.exception.InvalidAccountException;
import com.thl.banking.model.Account;
import com.thl.banking.model.User;
import com.thl.banking.validator.AccountValidation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AccountValidationImplTest {

    private AccountValidation accountValidation = new AccountValidationImpl();

    @Test
    public void checkIfNoError() {
        User user = new User(UUID.randomUUID().toString(),"testSenderName","");
        Account account = new Account();
        account.setUser(user);
        account.setAccountId(UUID.randomUUID().toString());
        account.setBalance(new BigDecimal(1000));
        Exception error = accountValidation.validate(account);
        assertThat(error).isNull();
    }

    @Test
    public void createErrorIfAccIdIsNull() {
        User user = new User(UUID.randomUUID().toString(),"testName","");
        Account account = new Account();
        account.setUser(user);
        account.setAccountId("");
        account.setBalance(new BigDecimal(100));
        Exception error = accountValidation.validate(account);// then
        assertThat(error).isNotNull();
        assertThat(error).isInstanceOf(InvalidAccountException.class);
        assertThat(error.getMessage()).isEqualTo("INVALID_ACCOUNT_EXCEPTION:EMPTY_ACCOUNT_ID");
    }


    @Test
    public void createErrorIfUserId() {
        User user = new User("","testName","");
        Account account = new Account();
        account.setUser(user);
        account.setAccountId(UUID.randomUUID().toString());
        account.setBalance(new BigDecimal(100));
        Exception error = accountValidation.validate(account);
        assertThat(error).isNotNull();
        assertThat(error).isInstanceOf(InvalidAccountException.class);
        assertThat(error.getMessage()).isEqualTo("INVALID_ACCOUNT_EXCEPTION:EMPTY_USER_ID");
    }

    @Test
    public void checkIfAccoutIsNull() {
        Exception error = accountValidation.validate(null);
        assertThat(error).isNotNull();
        assertThat(error).isInstanceOf(InvalidAccountException.class);
        assertThat(error.getMessage()).isEqualTo("INVALID_ACCOUNT_EXCEPTION:ACCOUNT_DOESNT_EXIST");
    }

    @Test
    public void checkIfNameIsEmpty() {
        User user = new User(UUID.randomUUID().toString(),"","");
        Account account = new Account();
        account.setUser(user);
        account.setAccountId(UUID.randomUUID().toString());
        account.setBalance(new BigDecimal(100));
        Exception error = accountValidation.validate(account);
        assertThat(error).isNotNull();
        assertThat(error).isInstanceOf(InvalidAccountException.class);
        assertThat(error.getMessage()).isEqualTo("INVALID_ACCOUNT_EXCEPTION:EMPTY_NAME");
    }


}
