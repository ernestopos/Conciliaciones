package com.conciliaciones.orchestrator.infrastructure.adapter.out.messaging;

import com.conciliaciones.orchestrator.application.port.out.OrchestrationEventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrchestrationEventPublisherAdapter implements OrchestrationEventPublisherPort {

    @Override
    public void publishProcessStarted(Long processId, String processCode) {
        log.info("Evento PROCESS_STARTED publicado. processId={}, processCode={}", processId, processCode);
    }

    @Override
    public void publishStepRequested(Long processId, String stepName) {
        log.info("Evento STEP_REQUESTED publicado. processId={}, step={}", processId, stepName);
    }

    @Override
    public void publishProcessFailed(Long processId, String reason) {
        log.error("Evento PROCESS_FAILED publicado. processId={}, reason={}", processId, reason);
    }
}
