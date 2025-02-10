package org.example.backend.exception.privilege;

public class PrivilegeNotSavedException extends RuntimeException {
  public PrivilegeNotSavedException(String message) {
    super(message);
  }
}
