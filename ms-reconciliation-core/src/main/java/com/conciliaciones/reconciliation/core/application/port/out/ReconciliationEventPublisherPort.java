package com.conciliaciones.reconciliation.core.application.port.out;

public interface ReconciliationEventPublisherPort {
    void publishStarted(Long reconciliationId);
    void publishCompleted(Long reconciliationId);
    void publishFailed(Long reconciliationId, String reason);
}
