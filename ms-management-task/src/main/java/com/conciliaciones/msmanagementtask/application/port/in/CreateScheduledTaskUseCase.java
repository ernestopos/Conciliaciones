package com.conciliaciones.msmanagementtask.application.port.in;

public interface CreateScheduledTaskUseCase {
    Long create(CreateScheduledTaskCommand command);
}
