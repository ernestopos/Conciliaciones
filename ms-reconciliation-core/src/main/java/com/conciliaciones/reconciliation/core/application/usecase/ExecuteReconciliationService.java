package com.conciliaciones.reconciliation.core.application.usecase;

import com.conciliaciones.reconciliation.core.application.port.in.ExecuteReconciliationUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.ReconciliationEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecuteReconciliationService implements ExecuteReconciliationUseCase {

    private final ReconciliationEventPublisherPort reconciliationEventPublisherPort;

    @Override
    public void execute(Long reconciliationId) {
        log.info("Iniciando conciliación {}", reconciliationId);
        reconciliationEventPublisherPort.publishStarted(reconciliationId);

        // Cascarón base: aquí irá la orquestación real del proceso de conciliación.

        reconciliationEventPublisherPort.publishCompleted(reconciliationId);
        log.info("Conciliación {} finalizada", reconciliationId);
    }
}
