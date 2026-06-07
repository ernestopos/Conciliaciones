package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RecalculateCommissionPaymentRequest(

        @NotNull(message = "Net Amount es obligatorio")
        @DecimalMin(value = "0.01", message = "Net Amount debe ser mayor a cero")
        BigDecimal netAmount,

        @NotNull(message = "Rate es obligatorio")
        @DecimalMin(value = "0.01", message = "Rate debe ser mayor a cero")
        BigDecimal rate,

        @NotNull(message = "Commission es obligatorio")
        @DecimalMin(value = "0.01", message = "Commission debe ser mayor a cero")
        BigDecimal commissionRatePct
) {
}