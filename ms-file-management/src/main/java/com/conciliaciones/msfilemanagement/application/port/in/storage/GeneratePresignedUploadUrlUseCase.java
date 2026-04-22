package com.conciliaciones.msfilemanagement.application.port.in.storage;

public interface GeneratePresignedUploadUrlUseCase {
    GeneratePresignedUploadUrlResult generate(GeneratePresignedUploadUrlCommand command);
}
