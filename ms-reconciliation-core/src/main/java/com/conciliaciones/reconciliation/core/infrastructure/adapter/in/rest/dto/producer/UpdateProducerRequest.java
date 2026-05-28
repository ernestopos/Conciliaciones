    package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer;

    import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

    public record UpdateProducerRequest(
            Long agencyId,
        String externalProducerId,
        String firstName,
        String lastName,
        @NotBlank String fullName,
        String email,
        String phone,
        String npn,
        String taxIdMasked,
        @NotNull Boolean active
    ) {
    }
