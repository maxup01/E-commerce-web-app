package org.example.backend.exception.privilege;

public class PrivilegeNotUpdatedException extends RuntimeException {
    public PrivilegeNotUpdatedException(String message) {
        super(message);
    }
}
