package com.conciliaciones.persistence.projection;

import java.math.BigDecimal;

public record CommissionReportRow(
        String producerName,
        String policyNumber,
        BigDecimal policyAmount,
        BigDecimal commissionRate,
        BigDecimal producerRate,
        BigDecimal commissionAmount,
        BigDecimal paymentAmount
) {
}