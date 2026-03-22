package com.conciliaciones.reconciliation.core.application.usecase;

import com.conciliaciones.reconciliation.core.application.port.in.GetReconciliationUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.ReconciliationPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.ReconciliationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetReconciliationService implements GetReconciliationUseCase {

    private final ReconciliationPersistencePort reconciliationPersistencePort;

    @Override
    public ReconciliationResponse getById(Long id) {
        var reconciliation = reconciliationPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conciliación no encontrada: " + id));

        return new ReconciliationResponse(
                reconciliation.getId(),
                reconciliation.getBusinessCode(),
                reconciliation.getSourceFileName(),
                reconciliation.getStatus().name()
        );
    }
}
