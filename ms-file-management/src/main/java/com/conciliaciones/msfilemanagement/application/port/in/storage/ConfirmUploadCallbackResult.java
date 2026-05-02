package com.conciliaciones.msfilemanagement.application.port.in.storage;
public record ConfirmUploadCallbackResult(Long sourceFileId, Long processingStatusId, String processingStatus) {}
