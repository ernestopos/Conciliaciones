package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionReconciliation;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateCommissionPaymentRequest {

    private Long policyStatusId;

    @DecimalMin(value = "0.00", message = "El valor netAmount no puede ser menor a cero")
    private BigDecimal netAmount;

    @DecimalMin(value = "0.01", message = "El valor rate debe ser mayor a cero")
    private BigDecimal rate;

    @DecimalMin(value = "0.01", message = "El valor commissionRatePct debe ser mayor a cero")
    private BigDecimal commissionRatePct;
}