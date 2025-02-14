package org.example.backend.exception.transaction;

public class OrderTransactionNotFoundException extends RuntimeException {
    public OrderTransactionNotFoundException(String message) {
        super(message);
    }
}
