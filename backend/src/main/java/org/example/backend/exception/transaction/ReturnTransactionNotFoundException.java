package org.example.backend.exception.transaction;

public class ReturnTransactionNotFoundException extends RuntimeException {
    public ReturnTransactionNotFoundException(String message) {
        super(message);
    }
}
