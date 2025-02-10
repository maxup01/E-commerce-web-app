package org.example.backend.exception.role;

public class PrivilegeNotUpdatedException extends RuntimeException {
    public PrivilegeNotUpdatedException(String message) {
        super(message);
    }
}
