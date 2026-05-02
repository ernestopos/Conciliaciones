package com.conciliaciones.reconciliation.core.infrastructure.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
