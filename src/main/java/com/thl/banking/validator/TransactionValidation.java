package com.thl.banking.validator;

import com.thl.banking.model.Transaction;

public interface TransactionValidation {
    Exception validate(Transaction transaction);
}
