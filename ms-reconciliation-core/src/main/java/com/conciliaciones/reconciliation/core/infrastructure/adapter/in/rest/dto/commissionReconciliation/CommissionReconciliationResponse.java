package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionReconciliation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionReconciliationResponse {

    private Long commissionStatementItemId;
    private Long commissionStatementId;

    private Long policyId;
    private String policyNumber;

    private Long policyStatusId;
    private String policyStatusName;

    private Long clientId;
    private String clientName;

    private Long producerId;
    private String producerName;

    private Long agencyId;
    private String agencyName;

    private Long carrierId;
    private String carrierName;

    private BigDecimal netAmount;
    private BigDecimal rate;
    private BigDecimal commissionRatePct;

    /**
     * Valor estimado que se pagaría una vez
     * la conciliación sea corregida.
     */
    private BigDecimal estimatedPaymentAmount;

    /**
     * INVALID_POLICY_STATUS
     * NET_AMOUNT_NEGATIVE
     */
    private String reconciliationType;

    /**
     * Descripción amigable del motivo.
     */
    private String reconciliationReason;

    private LocalDateTime createdAt;
}