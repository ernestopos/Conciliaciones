package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateManualCommissionPaymentRequest(
        @NotNull(message = "La póliza es obligatoria")
        Long policyId,

        @NotNull(message = "El productor es obligatorio")
        Long producerId,

        Long agencyId,

        LocalDate statementDate,
        LocalDate paidDate,
        String invoiceNumber,

        @NotNull(message = "El net amount es obligatorio")
        @DecimalMin(value = "0.01", message = "El net amount debe ser mayor a cero")
        BigDecimal netAmount,

        @NotNull(message = "La tasa es obligatoria")
        @DecimalMin(value = "0.01", message = "La tasa debe ser mayor a cero")
        BigDecimal rate,

        @NotNull(message = "El porcentaje de comisión es obligatorio")
        @DecimalMin(value = "0.01", message = "El porcentaje de comisión debe ser mayor a cero")
        BigDecimal commissionRatePct,

        Boolean includedForPayment,
        String concept
) {
}
