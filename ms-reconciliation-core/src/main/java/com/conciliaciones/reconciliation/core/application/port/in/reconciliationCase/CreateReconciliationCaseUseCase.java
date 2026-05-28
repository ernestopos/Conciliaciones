package com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.CreateReconciliationCaseRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.ReconciliationCaseResponse;

public interface CreateReconciliationCaseUseCase {
    ReconciliationCaseResponse create(CreateReconciliationCaseRequest request, String username);
}
