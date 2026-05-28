    package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase;

    import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

    public record CreateReconciliationCaseRequest(
            @NotNull Long sourceFileId,
        Long commissionStatementId,
        Long commissionStatementItemId,
        @NotNull Long carrierId,
        Long policyId,
        Long producerId,
        @NotNull Long caseTypeId,
        @NotNull Long severityId,
        @NotNull Long statusId,
        @NotNull LocalDateTime detectedAt,
        @NotBlank String description,
        String suggestedAction,
        String resolutionNotes,
        LocalDateTime resolvedAt,
        String resolvedBy
    ) {
    }
