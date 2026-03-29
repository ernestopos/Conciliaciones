package com.conciliaciones.security.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Respuesta de autenticación")
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        long refreshExpiresIn,
        OffsetDateTime issuedAt,
        AuthenticatedUserResponse user
) {
}
