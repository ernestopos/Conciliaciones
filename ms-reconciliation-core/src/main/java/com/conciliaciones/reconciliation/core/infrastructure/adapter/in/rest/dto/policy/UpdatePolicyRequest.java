package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdatePolicyRequest(
        @NotNull Long carrierId,
        Long clientId,
        @Size(max = 150) String policyNumber,
        @Size(max = 150) String subscriberId,
        LocalDate effectiveDate,
        LocalDate issueDate,
        LocalDate terminationDate,
        @NotNull Long statusId,
        @NotNull Long residentCityId,
        @Size(max = 100) String issueState,
        Integer membersCount,
        @Size(max = 255) String sourceKey,
        @NotNull Boolean active
) {
}