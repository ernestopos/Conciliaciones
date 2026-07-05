package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUser;

import java.time.LocalDateTime;

public record SecurityUserResponse(
        Long id,
        String username,
        String email,
        String fullName,
        Boolean active,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
) {
}