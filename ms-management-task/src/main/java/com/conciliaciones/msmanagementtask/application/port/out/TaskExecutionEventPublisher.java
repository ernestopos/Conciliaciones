package com.conciliaciones.msmanagementtask.application.port.out;

import com.conciliaciones.msmanagementtask.application.event.TaskExecutionRequestedEvent;

public interface TaskExecutionEventPublisher {
    void publish(TaskExecutionRequestedEvent event);
}
