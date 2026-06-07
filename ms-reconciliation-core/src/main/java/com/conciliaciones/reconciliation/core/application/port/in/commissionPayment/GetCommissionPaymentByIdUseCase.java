package com.conciliaciones.reconciliation.core.application.port.in.commissionPayment;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment.CommissionPaymentResponse;

public interface GetCommissionPaymentByIdUseCase {

    CommissionPaymentResponse getById(Long id);
}