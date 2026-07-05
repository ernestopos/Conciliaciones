package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu;

import java.util.List;

public record SecurityMenuTreeResponse(
        Long id,
        Long parameterId,
        String parameterCode,
        String code,
        String label,
        String icon,
        Integer sortOrder,
        Boolean active,
        List<SecuritySubMenuResponse> children
) {
}
