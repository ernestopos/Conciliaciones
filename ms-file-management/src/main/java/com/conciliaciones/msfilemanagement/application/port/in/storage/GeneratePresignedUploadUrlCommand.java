package com.conciliaciones.msfilemanagement.application.port.in.storage;

public record GeneratePresignedUploadUrlCommand(
        String bucketName,
        String fileName,
        String contentType,
        String folder,
        boolean createBucketPerUpload
) {
}
