package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole;

import java.time.LocalDateTime;

public record SecurityRoleResponse(
        Long id,
        String code,
        String name,
        String description,
        Boolean active,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
) {
}
