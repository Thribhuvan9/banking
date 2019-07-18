package com.thl.banking.validator.impl;

import com.thl.banking.exception.CurrentcyMismatchException;
import com.thl.banking.exception.InvalidAccountException;
import com.thl.banking.exception.TransferBetweenSameAccountException;
import com.thl.banking.model.Transaction;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.validator.TransactionValidation;

import javax.inject.Inject;

public class TransactionValidationImpl implements TransactionValidation {


    private AccountDAO accountDAO;

    @Inject
    public TransactionValidationImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public Exception validate(Transaction transaction) {
        if (!accountDAO.getAccountByAccountNumber(transaction.from().getAccountId()).isPresent()) {
            return new InvalidAccountException("ACCOUNT_DOESNT_EXIST");
        } else if (!accountDAO.getAccountByAccountNumber(transaction.to().getAccountId()).isPresent()) {
            return new InvalidAccountException("ACCOUNT_DOESNT_EXIST");
        } else if (!transaction.from().getCurrencyUnit().equals(transaction.to().getCurrencyUnit())) {
            return new CurrentcyMismatchException();
        } else if (transaction.from().getAccountId().equals(transaction.to().getAccountId())) {
            return new TransferBetweenSameAccountException();
        }
        return null;
    }
}
