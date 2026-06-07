package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CommissionPaymentResponse(
        Long id,
        Long policyId,
        String policyName,
        Long commissionStatementId,
        Long producerId,
        String producerName,
        Long agencyId,
        String agencyName,
        Long carrierId,
        String carrierName,
        BigDecimal netAmount,
        BigDecimal rate,
        BigDecimal commissionRatePct,
        BigDecimal paymentAmount,
        Boolean includedForPayment,
        LocalDateTime createdAt
) {
}