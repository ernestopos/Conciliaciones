package com.conciliaciones.mssecurity.domain.exception;

public class SecureBrowserLaunchException extends RuntimeException {
    public SecureBrowserLaunchException(String message) {
        super(message);
    }
    public SecureBrowserLaunchException(String message,Throwable cause) {
        super(message, cause);
    }
}
