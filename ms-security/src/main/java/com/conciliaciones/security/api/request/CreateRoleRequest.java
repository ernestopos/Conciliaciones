package com.conciliaciones.security.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud para crear rol")
public record CreateRoleRequest(
        @NotBlank @Schema(example = "ANALYST") String roleName,
        @Schema(example = "Rol funcional para analistas") String description
) {
}
