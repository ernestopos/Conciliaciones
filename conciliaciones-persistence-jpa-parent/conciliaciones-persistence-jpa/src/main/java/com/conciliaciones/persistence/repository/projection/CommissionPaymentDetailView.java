package com.conciliaciones.persistence.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CommissionPaymentDetailView {
    Long getId();
    Long getPolicyId();
    String getPolicyName();
    Long getCommissionStatementId();
    Long getProducerId();
    String getProducerName();
    BigDecimal getNetAmount();
    BigDecimal getRate();
    BigDecimal getCommissionRatePct();
    BigDecimal getPaymentAmount();
    Boolean getIncludedForPayment();
    LocalDateTime getCreatedAt();
    Long getAgencyId();
    String getAgencyName();
    Long getCarrierId();
    String getCarrierName();
}