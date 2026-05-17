package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("startPolicyRulesTask")
public class StartPolicyRulesTask extends AbstractManagementTask {

    @Override
    protected void doExecute(ScheduledTaskEntity task) {
        log.info("Ejecutando reglas de póliza/comisión. executionPlanTaskId={}, taskId={}",
                task.getExecutionPlanTask().getId(), task.getId());
        // TODO: invocar/publicar evento para evaluar commission_rule y detectar reconciliation_case.
    }
}
