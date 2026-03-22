package com.conciliaciones.orchestrator.application.usecase;

import com.conciliaciones.orchestrator.application.port.in.AdvanceProcessUseCase;
import com.conciliaciones.orchestrator.application.port.out.OrchestrationEventPublisherPort;
import com.conciliaciones.orchestrator.application.port.out.ReconciliationClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvanceProcessService implements AdvanceProcessUseCase {

    private final OrchestrationEventPublisherPort orchestrationEventPublisherPort;
    private final ReconciliationClientPort reconciliationClientPort;

    @Override
    public void advance(Long processId) {
        log.info("Avanzando proceso {}", processId);
        orchestrationEventPublisherPort.publishStepRequested(processId, "RECONCILIATION");
        reconciliationClientPort.triggerReconciliation(processId);
    }
}
