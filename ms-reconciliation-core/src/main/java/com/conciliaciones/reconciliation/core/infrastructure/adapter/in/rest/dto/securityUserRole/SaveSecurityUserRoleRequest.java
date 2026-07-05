package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUserRole;

import jakarta.validation.constraints.NotNull;

public record SaveSecurityUserRoleRequest(
        @NotNull Long userId,
        @NotNull Long roleId
) {
}