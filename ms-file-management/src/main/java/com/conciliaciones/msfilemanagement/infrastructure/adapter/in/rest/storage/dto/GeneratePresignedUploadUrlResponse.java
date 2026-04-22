package com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.storage.dto;

public record GeneratePresignedUploadUrlResponse(
        String bucketName,
        String objectKey,
        String presignedUrl,
        long expiresInMinutes
) {
}
