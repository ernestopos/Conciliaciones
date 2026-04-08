package com.conciliaciones.security.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud de cierre de sesión")
public record LogoutRequest(
        @NotBlank
        @Schema(example = "eyJhbGciOi...")
        String refreshToken
) {
}
