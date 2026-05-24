package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreatePolicyRequest(
        @NotNull Long carrierId,
        Long clientId,
        @NotBlank String policyNumber,
        String subscriberId,
        LocalDate effectiveDate,
        LocalDate issueDate,
        LocalDate terminationDate,
        @NotNull Long statusId,
        String residentState,
        String issueState,
        Integer membersCount,
        String sourceKey,
        @NotNull Boolean active
) {
}
