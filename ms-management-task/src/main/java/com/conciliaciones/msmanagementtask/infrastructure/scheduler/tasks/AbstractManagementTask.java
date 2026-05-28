package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.msmanagementtask.infrastructure.scheduler.ScheduledTaskRunnable;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractManagementTask implements ScheduledTaskRunnable {

    @Override
    public void execute(ScheduledTaskEntity task) {
        log.info("LOG INICIO X = executeTaskBusinessLogic - taskId={}, executionPlanTaskId={}, taskType={}",
                task.getId(),
                task.getExecutionPlanTask().getId(),
                task.getTaskType().getName());

        doExecute(task);

        log.info("LOG FIN X = executeTaskBusinessLogic - taskId={}, executionPlanTaskId={}, taskType={}",
                task.getId(),
                task.getExecutionPlanTask().getId(),
                task.getTaskType().getName());
    }

    protected abstract void doExecute(ScheduledTaskEntity task);
}
