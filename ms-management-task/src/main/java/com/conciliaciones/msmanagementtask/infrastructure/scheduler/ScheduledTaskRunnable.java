package com.conciliaciones.msmanagementtask.infrastructure.scheduler;

import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;

public interface ScheduledTaskRunnable {
    void execute(ScheduledTaskEntity task);
}
