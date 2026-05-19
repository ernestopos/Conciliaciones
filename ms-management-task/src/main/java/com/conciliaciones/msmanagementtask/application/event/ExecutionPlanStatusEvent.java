package com.conciliaciones.msmanagementtask.application.event;

import java.time.LocalDateTime;
import java.util.List;

public record ExecutionPlanStatusEvent(
        Long executionPlanTaskId,
        Long sourceFileId,
        String planExecuteCode,
        String planStatus,
        Boolean successful,
        String message,
        LocalDateTime eventDate,
        List<ScheduledTaskStatus> tasks
) {
    public record ScheduledTaskStatus(
            Long scheduledTaskId,
            String taskType,
            String status,
            Boolean active,
            Integer order
    ) {}
}
