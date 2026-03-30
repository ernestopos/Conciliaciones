package com.conciliaciones.security.domain.model;

import java.util.List;

public record SecurityUser(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        boolean emailVerified,
        List<String> roles
) {
}
