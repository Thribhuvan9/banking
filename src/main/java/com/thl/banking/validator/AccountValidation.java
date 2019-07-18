package com.thl.banking.validator;

import com.thl.banking.model.Account;

public interface AccountValidation {
    Exception validate(Account account);
}
