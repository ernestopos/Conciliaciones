package com.conciliaciones.persistence.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CommissionReconciliationView {
    Long getCommissionStatementItemId();
    Long getCommissionStatementId();

    Long getPolicyId();
    String getPolicyNumber();
    Long getPolicyStatusId();
    String getPolicyStatusName();

    Long getClientId();
    String getClientName();

    Long getProducerId();
    String getProducerName();

    Long getAgencyId();
    String getAgencyName();

    Long getCarrierId();
    String getCarrierName();

    BigDecimal getNetAmount();
    BigDecimal getRate();
    BigDecimal getCommissionRatePct();
    BigDecimal getEstimatedPaymentAmount();

    String getReconciliationType();
    String getReconciliationReason();

    LocalDateTime getCreatedAt();
}