package com.conciliaciones.msmanagementtask.application.port.out;

import com.conciliaciones.msmanagementtask.application.event.ExecutionPlanStatusEvent;

public interface ExecutionPlanEventPublisher {
    void publish(ExecutionPlanStatusEvent event);
}
