package com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.storage.dto;

import jakarta.validation.constraints.NotNull;

public record ConfirmUploadCallbackRequest(
        @NotNull Long sourceFileId,
        String bucketName,
        String objectKey,
        String fileName,
        String contentType,
        Long fileSizeBytes,
        boolean success,
        String errorMessage
) {}
