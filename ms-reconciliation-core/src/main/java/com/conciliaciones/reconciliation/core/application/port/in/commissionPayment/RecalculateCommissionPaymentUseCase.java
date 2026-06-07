package com.conciliaciones.reconciliation.core.application.port.in.commissionPayment;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment.CommissionPaymentResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment.RecalculateCommissionPaymentRequest;

public interface RecalculateCommissionPaymentUseCase {

    CommissionPaymentResponse recalculate(Long id, RecalculateCommissionPaymentRequest request);
}