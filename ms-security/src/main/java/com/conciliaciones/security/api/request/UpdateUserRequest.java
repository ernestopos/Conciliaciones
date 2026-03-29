package com.conciliaciones.security.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud para actualizar usuario")
public record UpdateUserRequest(
        @NotBlank @Email @Schema(example = "jperez@empresa.com") String email,
        @NotBlank @Schema(example = "Juan") String firstName,
        @NotBlank @Schema(example = "Pérez") String lastName,
        @Schema(example = "true") boolean enabled
) {
}
