package com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto;

public record UserRepresentation(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        Boolean enabled
) {
}