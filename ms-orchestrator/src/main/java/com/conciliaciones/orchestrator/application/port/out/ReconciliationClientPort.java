package com.conciliaciones.orchestrator.application.port.out;

public interface ReconciliationClientPort {
    void triggerReconciliation(Long processId);
}
