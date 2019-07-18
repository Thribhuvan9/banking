package com.thl.banking.exception;

public class AccountAlreadyExistsException extends RuntimeException {

    private final String accountNumber;

    public AccountAlreadyExistsException(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String getMessage() {
        return String.format("Account with number %s already exists", accountNumber);
    }
}
