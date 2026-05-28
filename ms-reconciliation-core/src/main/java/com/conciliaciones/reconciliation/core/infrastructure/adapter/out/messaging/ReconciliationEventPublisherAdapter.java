package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.messaging;

import com.conciliaciones.reconciliation.core.application.port.out.ReconciliationEventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReconciliationEventPublisherAdapter implements ReconciliationEventPublisherPort {

    @Override
    public void publishStarted(Long reconciliationId) {
        log.info("Evento STARTED publicado para conciliación {}", reconciliationId);
    }

    @Override
    public void publishCompleted(Long reconciliationId) {
        log.info("Evento COMPLETED publicado para conciliación {}", reconciliationId);
    }

    @Override
    public void publishFailed(Long reconciliationId, String reason) {
        log.error("Evento FAILED publicado para conciliación {}. Motivo: {}", reconciliationId, reason);
    }
}
