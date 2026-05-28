package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateReconciliationRequest(
        @NotBlank String businessCode,
        @NotBlank String sourceFileName
) {
}
