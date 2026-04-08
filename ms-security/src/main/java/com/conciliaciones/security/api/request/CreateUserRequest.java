package com.conciliaciones.security.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Solicitud para crear usuario")
public record CreateUserRequest(
        @NotBlank @Schema(example = "jperez") String username,
        @NotBlank @Email @Schema(example = "jperez@empresa.com") String email,
        @NotBlank @Schema(example = "Juan") String firstName,
        @NotBlank @Schema(example = "Pérez") String lastName,
        @NotBlank @Schema(example = "Temporal123*") String password,
        @Schema(example = "true") boolean enabled,
        @NotEmpty List<String> roles
) {
}
