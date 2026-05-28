package com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.ReconciliationCaseResponse;

public interface ReprocessReconciliationCaseUseCase {
    ReconciliationCaseResponse reprocess(Long id, String username);
}
