package com.conciliaciones.msmanagementtask.application.event;

import java.time.LocalDateTime;

public record TaskExecutionRequestedEvent(
        Long executionPlanTaskId,
        Long scheduledTaskId,
        Long sourceFileId,
        String taskType,
        String eventType,
        LocalDateTime eventDate
) {}
