package com.conciliaciones.reconciliation.core.application.usecase;

import com.conciliaciones.reconciliation.core.application.port.in.CreateReconciliationUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.ReconciliationPersistencePort;
import com.conciliaciones.reconciliation.core.domain.model.Reconciliation;
import com.conciliaciones.reconciliation.core.domain.model.ReconciliationStatus;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.CreateReconciliationRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.ReconciliationResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateReconciliationService implements CreateReconciliationUseCase {

    private final ReconciliationPersistencePort reconciliationPersistencePort;

    @Override
    public ReconciliationResponse create(CreateReconciliationRequest request) {
        Reconciliation saved = reconciliationPersistencePort.save(
                Reconciliation.builder()
                        .businessCode(request.businessCode())
                        .sourceFileName(request.sourceFileName())
                        .status(ReconciliationStatus.CREATED)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return new ReconciliationResponse(
                saved.getId(),
                saved.getBusinessCode(),
                saved.getSourceFileName(),
                saved.getStatus().name()
        );
    }
}
