package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask;

import java.time.LocalDateTime;

public record ExecutionPlanTaskResponse(
        Long id,
        Long sourceFileId,
        String originalFileName,
        Long statusId,
        String statusName,
        String planExecuteCode,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        Boolean successful,
        String message
) {
}
