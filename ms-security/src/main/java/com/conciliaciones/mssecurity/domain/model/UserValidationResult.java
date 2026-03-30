package com.conciliaciones.mssecurity.domain.model;

public record UserValidationResult(
        String username,
        boolean active,
        String message
) {
}
