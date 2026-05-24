package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAgencyRequest(
        @NotNull Long carrierId,
        String externalAgencyId,
        @NotBlank String name,
        @NotNull Boolean active
) {
}
