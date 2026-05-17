package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.sourceFile;

import java.time.LocalDateTime;

public record SourceFileResponse(
        Long id,
        Long carrierId,
        String originalFileName,
        String storedFileName,
        String fileExtension,
        String mimeType,
        Long fileSizeBytes,
        String s3Bucket,
        String s3Key,
        String checksum,
        String sourceSystem,
        LocalDateTime uploadDate,
        String uploadedBy,
        Long processingStatusId,
        String errorMessage,
        Integer totalRows,
        Integer processedRows,
        Integer failedRows
) {
}
