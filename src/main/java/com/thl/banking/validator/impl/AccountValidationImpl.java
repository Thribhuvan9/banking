package com.thl.banking.validator.impl;

import com.thl.banking.exception.InvalidAccountException;
import com.thl.banking.model.Account;
import com.thl.banking.validator.AccountValidation;

public class AccountValidationImpl implements AccountValidation {


    @Override
    public Exception validate(Account account) {
        if (account == null) {
            return new InvalidAccountException("ACCOUNT_DOESNT_EXIST");
        } else if (account.getAccountId() == null || account.getAccountId().isEmpty()) {
            return new InvalidAccountException("EMPTY_ACCOUNT_ID");
        } else if (account.getUser().getId() == null || account.getUser().getId().isEmpty()) {
            return new InvalidAccountException("EMPTY_USER_ID");
        } else if (account.getUser().getName() == null || account.getUser().getName().isEmpty()) {
            return new InvalidAccountException("EMPTY_NAME");
        }
        return null;
    }
}
