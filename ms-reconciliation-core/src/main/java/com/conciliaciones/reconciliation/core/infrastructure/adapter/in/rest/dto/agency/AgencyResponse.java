package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency;

import java.time.LocalDateTime;

public record AgencyResponse(
        Long id,
        Long carrierId,
        String carrierName,
        String externalAgencyId,
        String name,
        Boolean active,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
) {
}
