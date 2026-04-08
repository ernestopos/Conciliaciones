package com.conciliaciones.security.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Solicitud para asignar permisos a un rol")
public record AssignPermissionsRequest(
        @NotEmpty List<String> permissions
) {
}
