package com.conciliaciones.reconciliation.core.application.port.in.commissionReconciliation;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionReconciliation.CommissionReconciliationResponse;

public interface GetCommissionReconciliationByIdUseCase {

    CommissionReconciliationResponse getByCommissionStatementItemId(Long commissionStatementItemId);
}