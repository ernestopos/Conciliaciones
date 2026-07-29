package com.conciliaciones.persistence.projection;

import java.math.BigDecimal;

public interface PaymentMonthlyForCarrierReportRow {

    String getCarrierName();

    String getAgencyId();

    String getAgency();

    String getProducerId();

    String getProducer();

    BigDecimal getJanuary();

    BigDecimal getFebruary();

    BigDecimal getMarch();

    BigDecimal getApril();

    BigDecimal getMay();

    BigDecimal getJune();

    BigDecimal getJuly();

    BigDecimal getAugust();

    BigDecimal getSeptember();

    BigDecimal getOctober();

    BigDecimal getNovember();

    BigDecimal getDecember();

    BigDecimal getTotal();
}
