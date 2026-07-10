package com.conciliaciones.reporting.infrastructure.adapter.in.rest.enums;

public enum KindReports {

    PaymentForEachProducer,
    PaymentMonthlyForCarrier;

    public static KindReports getKindReports(String name){
        return KindReports.valueOf(name);
    }

}
