package com.conciliaciones.mssecurity.infrastructure.exception;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}
