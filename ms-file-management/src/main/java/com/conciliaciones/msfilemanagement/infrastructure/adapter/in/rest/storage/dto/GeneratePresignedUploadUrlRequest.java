package com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.storage.dto;

import jakarta.validation.constraints.NotBlank;

public record GeneratePresignedUploadUrlRequest(
        String bucketName,
        @NotBlank String fileName,
        @NotBlank String contentType,
        String folder,
        boolean createBucketPerUpload
) {
}
