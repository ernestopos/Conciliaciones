    package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier;

    import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

    public record CreateCarrierRequest(
            @NotBlank String code,
        @NotBlank String name,
        String description,
        @NotNull Boolean active
    ) {
    }
