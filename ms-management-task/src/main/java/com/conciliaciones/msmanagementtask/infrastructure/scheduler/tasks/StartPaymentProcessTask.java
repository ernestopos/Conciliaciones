package com.conciliaciones.msmanagementtask.infrastructure.scheduler.tasks;

import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("startPaymentProcessTask")
public class StartPaymentProcessTask extends AbstractManagementTask {

    @Override
    protected void doExecute(ScheduledTaskEntity task) {
        log.info("Ejecutando cálculo de pagos. executionPlanTaskId={}, taskId={}",
                task.getExecutionPlanTask().getId(), task.getId());
        // TODO: invocar/publicar evento para generar commission_payment y commission_payment_detail.
    }
}
