package com.conciliaciones.orchestrator.application.port.out;

public interface OrchestrationEventPublisherPort {
    void publishProcessStarted(Long processId, String processCode);
    void publishStepRequested(Long processId, String stepName);
    void publishProcessFailed(Long processId, String reason);
}
