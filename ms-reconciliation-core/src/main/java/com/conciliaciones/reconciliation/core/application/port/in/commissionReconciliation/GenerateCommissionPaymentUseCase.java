package com.conciliaciones.reconciliation.core.application.port.in.commissionReconciliation;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionReconciliation.CommissionReconciliationResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionReconciliation.GenerateCommissionPaymentRequest;

public interface GenerateCommissionPaymentUseCase {

    CommissionReconciliationResponse generatePayment(
            Long commissionStatementItemId,
            GenerateCommissionPaymentRequest request
    );
}