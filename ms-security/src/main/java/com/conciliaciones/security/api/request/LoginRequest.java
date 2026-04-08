package com.conciliaciones.security.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud de autenticación")
public record LoginRequest(
        @NotBlank
        @Schema(example = "admin-app")
        String username,
        @NotBlank
        @Schema(example = "AdminApp123*")
        String password
) {
}
