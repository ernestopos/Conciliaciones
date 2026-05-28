package com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.dto;

import com.conciliaciones.domain.file.SourceFile;
import com.conciliaciones.domain.processing.ProcessingStatus;

public record SourceFileResponse(
        Long id,
        Long clientId,
        String originalFileName,
        String storagePath,
        String mimeType,
        Long fileSize,
        String fileType,
        String checksum,
        ProcessingStatus processingStatus
) {
    public static SourceFileResponse from(SourceFile sourceFile) {
        return new SourceFileResponse(
                sourceFile.getId(),
                sourceFile.getClientId(),
                sourceFile.getOriginalFileName(),
                sourceFile.getStoragePath(),
                sourceFile.getMimeType(),
                sourceFile.getFileSize(),
                sourceFile.getFileType() != null ? sourceFile.getFileType().name() : null,
                sourceFile.getChecksum(),
                sourceFile.getProcessingStatus()
        );
    }
}
