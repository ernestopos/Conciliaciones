package com.conciliaciones.msfilemanagement.infrastructure.adapter.in.rest.storage.dto;

public record ConfirmUploadCallbackResponse(Long sourceFileId, Long processingStatusId, String processingStatus, String message) {}
