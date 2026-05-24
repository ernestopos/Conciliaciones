package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PolicyResponse(
        Long id,
        Long carrierId,
        String carrierName,
        Long clientId,
        String clientName,
        String policyNumber,
        String subscriberId,
        LocalDate effectiveDate,
        LocalDate issueDate,
        LocalDate terminationDate,
        Long statusId,
        String statusName,
        String residentState,
        String issueState,
        Integer membersCount,
        String sourceKey,
        Boolean active,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
) {
}
