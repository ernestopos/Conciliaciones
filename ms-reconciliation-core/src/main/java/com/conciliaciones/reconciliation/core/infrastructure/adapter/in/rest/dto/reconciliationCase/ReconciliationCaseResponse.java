package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase;

import java.time.LocalDateTime;

public record ReconciliationCaseResponse(
        Long id,
    Long sourceFileId,
    Long commissionStatementId,
    Long commissionStatementItemId,
    Long carrierId,
    Long policyId,
    Long producerId,
    Long caseTypeId,
    Long severityId,
    Long statusId,
    LocalDateTime detectedAt,
    String description,
    String suggestedAction,
    String resolutionNotes,
    LocalDateTime resolvedAt,
    String resolvedBy,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime updatedAt,
    String updatedBy
) {
}
