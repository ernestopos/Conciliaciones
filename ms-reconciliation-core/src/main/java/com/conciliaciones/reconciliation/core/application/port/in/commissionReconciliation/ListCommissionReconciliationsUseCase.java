package com.conciliaciones.reconciliation.core.application.port.in.commissionReconciliation;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionReconciliation.CommissionReconciliationResponse;

import java.util.List;

public interface ListCommissionReconciliationsUseCase {

    List<CommissionReconciliationResponse> list(
            String producerName,
            String policyNumber,
            String agencyName,
            String carrierName
    );
}