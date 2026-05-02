package com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.ReconciliationCaseResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.UpdateReconciliationCaseRequest;

public interface UpdateReconciliationCaseUseCase {
    ReconciliationCaseResponse update(Long id, UpdateReconciliationCaseRequest request, String username);
}
