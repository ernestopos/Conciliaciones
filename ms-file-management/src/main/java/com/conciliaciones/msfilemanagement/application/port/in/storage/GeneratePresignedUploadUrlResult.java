package com.conciliaciones.msfilemanagement.application.port.in.storage;

public record GeneratePresignedUploadUrlResult(
        Long sourceFileId,
        String bucketName,
        String objectKey,
        String presignedUrl,
        long expiresInMinutes
) {
}
