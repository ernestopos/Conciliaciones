package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter;

import java.time.LocalDateTime;

public record ParameterResponse(
        Long id,
    String name,
    String description,
    String value,
    String parameterGroup,
    Boolean active,
    Integer sortOrder,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime updatedAt,
    String updatedBy
) {
}
