package com.conciliaciones.reconciliation.core.application.port.in;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.ReconciliationResponse;

public interface GetReconciliationUseCase {
    ReconciliationResponse getById(Long id);
}
