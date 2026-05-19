package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("startValidateDataTask")
public class StartValidateDataTask extends AbstractManagementTask {

    @Override
    protected void doExecute(ScheduledTaskEntity task) {
        log.info("Ejecutando validación de datos. executionPlanTaskId={}, taskId={}",
                task.getExecutionPlanTask().getId(), task.getId());
        // TODO: publicar/consumir evento hacia ms-reconciliation-core para validación real del archivo.
    }
}
