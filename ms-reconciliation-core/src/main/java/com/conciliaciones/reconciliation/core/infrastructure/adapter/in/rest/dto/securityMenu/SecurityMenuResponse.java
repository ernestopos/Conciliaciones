package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu;

public record SecurityMenuResponse(
        Long id,
        Long parameterId,
        String parameterCode,
        String code,
        String label,
        String icon,
        Integer sortOrder,
        Boolean active
) {
}
