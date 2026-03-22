package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto;

public record ReconciliationResponse(
        Long id,
        String businessCode,
        String sourceFileName,
        String status
) {
}
