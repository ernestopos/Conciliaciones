package com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        List<String> roles
) {
}
