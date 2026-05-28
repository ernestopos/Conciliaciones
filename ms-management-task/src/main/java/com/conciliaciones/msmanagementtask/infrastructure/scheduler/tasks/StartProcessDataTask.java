package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.msmanagementtask.application.constants.ManagementTaskParameterGroups;
import com.conciliaciones.msmanagementtask.application.constants.ManagementTaskStatus;
import com.conciliaciones.persistence.jpa.entity.ExecutionPlanTaskEntity;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.repository.ExecutionPlanTaskRepository;
import com.conciliaciones.persistence.repository.ParameterRepository;
import com.conciliaciones.persistence.repository.ScheduledTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("startProcessDataTask")
public class StartProcessDataTask extends AbstractManagementTask {

    @Override
    protected void doExecute(ScheduledTaskEntity task) {
        log.info("Ejecutando procesamiento/normalización de datos. executionPlanTaskId={}, taskId={}", task.getExecutionPlanTask().getId(), task.getId());
    }
}
