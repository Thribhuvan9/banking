package com.thl.banking.exception;

public class InvalidAccountException extends RuntimeException {

    public InvalidAccountException(String message) {
        super("INVALID_ACCOUNT_EXCEPTION:" + message);
    }

}
