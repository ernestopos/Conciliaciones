    package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client;

    import java.time.LocalDate;
import java.time.LocalDateTime;

    public record ClientResponse(
            Long id,
        String externalClientId,
        String firstName,
        String middleName,
        String lastName,
        String fullName,
        LocalDate birthDate,
        String state,
        Boolean active,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
    ) {
    }
