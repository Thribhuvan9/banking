package com.thl.banking.exception;

public class TransferBetweenSameAccountException extends RuntimeException {
    @Override
    public String getMessage() {
        return String.format(
                "Sender and receiver account are same. Cannot transfer money between same account."
        );
    }
}
