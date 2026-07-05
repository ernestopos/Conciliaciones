package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRoleMenuPermission;

public record SecurityRoleMenuPermissionResponse(
        Long id,
        Long roleId,
        String roleCode,
        String roleName,
        Long menuId,
        String menuCode,
        Long subMenuId,
        String subMenuCode,
        String subMenuLabel,
        String route,
        Boolean active
) {
}