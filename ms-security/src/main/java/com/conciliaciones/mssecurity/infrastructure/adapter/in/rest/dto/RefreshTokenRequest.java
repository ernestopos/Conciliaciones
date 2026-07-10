package com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "El refreshToken es obligatorio")
        String refreshToken
) {
}
