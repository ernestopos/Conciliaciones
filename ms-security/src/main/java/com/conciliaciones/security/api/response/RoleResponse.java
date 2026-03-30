package com.conciliaciones.security.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Información de rol")
public record RoleResponse(
        String roleName,
        String description,
        List<String> permissions
) {
}
