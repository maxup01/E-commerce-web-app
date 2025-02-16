package org.example.backend.exception.transaction;

public class ReturnedProductNotFoundException extends RuntimeException {
    public ReturnedProductNotFoundException(String message) {
        super(message);
    }
}
