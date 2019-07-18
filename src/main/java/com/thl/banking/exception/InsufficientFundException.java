package com.thl.banking.exception;

public class InsufficientFundException extends RuntimeException {
    private String accountNumber;

    public InsufficientFundException(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override public String getMessage() {
        return String.format("Insufficient fund on the account with number %s", accountNumber);
    }
}
