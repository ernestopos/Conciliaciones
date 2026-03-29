package com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto;

public record ValidateResponse(
        String username,
        boolean active,
        String message
) {
}
