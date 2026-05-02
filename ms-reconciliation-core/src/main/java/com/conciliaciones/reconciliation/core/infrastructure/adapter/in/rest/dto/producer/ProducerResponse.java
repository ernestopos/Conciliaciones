package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer;

import java.time.LocalDateTime;

public record ProducerResponse(
        Long id,
    Long agencyId,
    String externalProducerId,
    String firstName,
    String lastName,
    String fullName,
    String email,
    String phone,
    String npn,
    String taxIdMasked,
    Boolean active,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime updatedAt,
    String updatedBy
) {
}
