package org.example.backend.exception.role;

public class RoleNotSavedException extends RuntimeException {
    public RoleNotSavedException(String message) {
        super(message);
    }
}
