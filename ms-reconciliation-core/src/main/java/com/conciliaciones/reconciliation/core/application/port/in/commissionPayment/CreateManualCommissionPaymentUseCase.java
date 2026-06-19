package com.conciliaciones.reconciliation.core.application.port.in.commissionPayment;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment.CommissionPaymentResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment.CreateManualCommissionPaymentRequest;

public interface CreateManualCommissionPaymentUseCase {

    CommissionPaymentResponse createManual(CreateManualCommissionPaymentRequest request, String username);
}
