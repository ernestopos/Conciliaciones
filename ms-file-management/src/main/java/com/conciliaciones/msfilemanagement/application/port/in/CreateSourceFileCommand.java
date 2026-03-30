package com.conciliaciones.msfilemanagement.application.port.in;

import com.conciliaciones.domain.file.FileType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSourceFileCommand(
        @NotNull Long clientId,
        @NotBlank String originalFileName,
        @NotBlank String storagePath,
        @NotBlank String mimeType,
        @NotNull @Min(1) Long fileSize,
        @NotNull FileType fileType,
        @NotBlank String checksum,
        String createdBy
) {
}
