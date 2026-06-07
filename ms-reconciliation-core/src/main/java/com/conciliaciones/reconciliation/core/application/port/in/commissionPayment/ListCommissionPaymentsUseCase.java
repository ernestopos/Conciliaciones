package com.conciliaciones.reconciliation.core.application.port.in.commissionPayment;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment.CommissionPaymentResponse;

import java.util.List;

public interface ListCommissionPaymentsUseCase {

    List<CommissionPaymentResponse> list(
            String producerName,
            String policyNumber,
            String agencyName,
            String carrierName
    );
}