package org.example.backend.exception.image;

public class ProductPageImageNotFoundException extends RuntimeException {
    public ProductPageImageNotFoundException(String message) {
        super(message);
    }
}
