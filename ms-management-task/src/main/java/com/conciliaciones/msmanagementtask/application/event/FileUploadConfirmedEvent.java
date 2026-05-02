package com.conciliaciones.msmanagementtask.application.event;

public record FileUploadConfirmedEvent(
        Long sourceFileId,
        String bucketName,
        String objectKey,
        String fileName,
        String eventType
) {
}
