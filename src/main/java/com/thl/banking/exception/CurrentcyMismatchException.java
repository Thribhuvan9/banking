package com.thl.banking.exception;

public class CurrentcyMismatchException extends RuntimeException{

    @Override
    public String getMessage() {
        return String.format(
                "Cannot transfer money between account have funds in different currencies"
        );
    }
}
