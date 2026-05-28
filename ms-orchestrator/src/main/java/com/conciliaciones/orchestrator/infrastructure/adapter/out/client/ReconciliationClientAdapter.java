package com.conciliaciones.orchestrator.infrastructure.adapter.out.client;

import com.conciliaciones.orchestrator.application.port.out.ReconciliationClientPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReconciliationClientAdapter implements ReconciliationClientPort {

    @Override
    public void triggerReconciliation(Long processId) {
        log.info("Invocación dummy a ms-reconciliation-core para processId={}", processId);
    }
}
