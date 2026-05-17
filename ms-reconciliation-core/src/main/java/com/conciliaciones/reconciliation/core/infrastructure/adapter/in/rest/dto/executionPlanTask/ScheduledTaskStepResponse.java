package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask;

import java.time.LocalDateTime;

public record ScheduledTaskStepResponse(
        Long id,
        Long taskTypeId,
        String taskTypeName,
        Long statusId,
        String statusName,
        Integer taskOrder,
        String cronExpression,
        String taskBeanName,
        String methodName,
        String payload,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
