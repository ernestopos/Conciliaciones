package com.conciliaciones.reconciliation.core.application.port.in;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.CreateReconciliationRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.ReconciliationResponse;

public interface CreateReconciliationUseCase {
    ReconciliationResponse create(CreateReconciliationRequest request);
}
