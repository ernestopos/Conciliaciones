package com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GeneratePresignedUploadUrlRequest(
        String bucketName,
        @NotBlank String fileName,
        @NotBlank String contentType,
        @NotNull Long carrierId,
        String folder,
        boolean createBucketPerUpload
) {
}
