package org.example.backend.exception.logistic;

public class DeliveryProviderNotFoundException extends RuntimeException {
    public DeliveryProviderNotFoundException(String message) {
        super(message);
    }
}
