package org.example.backend.exception.product;

public class ProductNotSavedException extends RuntimeException {
    public ProductNotSavedException(String message) {
        super(message);
    }
}
