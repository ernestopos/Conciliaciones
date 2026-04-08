package com.conciliaciones.security.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Información del usuario autenticado")
public record AuthenticatedUserResponse(
        String id,
        String username,
        String email,
        String fullName,
        List<String> roles,
        List<String> permissions
) {
}
