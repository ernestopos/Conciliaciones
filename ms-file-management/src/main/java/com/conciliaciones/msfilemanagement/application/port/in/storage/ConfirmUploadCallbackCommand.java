package com.conciliaciones.msfilemanagement.application.port.in.storage;
public record ConfirmUploadCallbackCommand(Long sourceFileId, String bucketName, String objectKey, String fileName, String contentType, Long fileSizeBytes, boolean success, String errorMessage) {}
