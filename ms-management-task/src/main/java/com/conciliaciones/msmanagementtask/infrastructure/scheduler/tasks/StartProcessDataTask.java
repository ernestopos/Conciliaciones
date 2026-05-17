package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("startProcessDataTask")
public class StartProcessDataTask extends AbstractManagementTask {

    @Override
    protected void doExecute(ScheduledTaskEntity task) {
        log.info("Ejecutando procesamiento/normalización de datos. executionPlanTaskId={}, taskId={}",
                task.getExecutionPlanTask().getId(), task.getId());
        // TODO: invocar/publicar evento para normalizar raw_import_record -> policy/client/producer/statement.
    }
}
