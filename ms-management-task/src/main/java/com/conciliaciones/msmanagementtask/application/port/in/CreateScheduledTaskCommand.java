package com.conciliaciones.msmanagementtask.application.port.in;

import com.conciliaciones.domain.task.TaskType;

public record CreateScheduledTaskCommand(
        Long sourceFileId,
        TaskType taskType,
        String payload,
        String createdBy
) {
}
