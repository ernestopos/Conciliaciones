package com.conciliaciones.security.domain.model;

import java.util.List;

public record SecurityRole(
        String roleName,
        String description,
        List<String> permissions
) {
}
