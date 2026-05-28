package com.conciliaciones.msfilemanagement.application.event;

public record FileUploadConfirmedEvent(
        Long sourceFileId,
        String bucketName,
        String objectKey,
        String fileName,
        String eventType
) {
}
