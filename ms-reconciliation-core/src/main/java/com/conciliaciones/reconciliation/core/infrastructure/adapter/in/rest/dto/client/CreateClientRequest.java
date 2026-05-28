    package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client;

    import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

    public record CreateClientRequest(
            String externalClientId,
        String firstName,
        String middleName,
        String lastName,
        @NotBlank String fullName,
        LocalDate birthDate,
        String state,
        @NotNull Boolean active
    ) {
    }
