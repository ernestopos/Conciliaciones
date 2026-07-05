package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUserRole;

public record SecurityUserRoleResponse(
        Long id,
        Long userId,
        String username,
        String email,
        String fullName,
        Long roleId,
        String roleCode,
        String roleName,
        Boolean active
) {
}