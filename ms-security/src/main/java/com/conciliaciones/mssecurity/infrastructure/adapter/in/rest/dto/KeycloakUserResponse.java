package com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto;

public record KeycloakUserResponse(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        Boolean enabled,
        String role
) {
}