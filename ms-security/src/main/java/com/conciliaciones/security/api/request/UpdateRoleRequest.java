package com.conciliaciones.security.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud para actualizar rol")
public record UpdateRoleRequest(
        @NotBlank @Schema(example = "Rol actualizado") String description
) {
}
