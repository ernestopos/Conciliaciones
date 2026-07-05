package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu;

public record SecuritySubMenuResponse(
        Long id,
        Long menuId,
        String menuCode,
        Long parameterId,
        String parameterCode,
        String code,
        String label,
        String route,
        String icon,
        Integer sortOrder,
        Boolean active
) {
}
