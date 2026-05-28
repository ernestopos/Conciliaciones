package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask;

import java.time.LocalDateTime;

public record ValidationExecutionDetailResponse(
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Boolean successful,
        String message,
        String validationTypeDescription,
        String validationStatusDescription
) {
}
