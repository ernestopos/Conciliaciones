package com.conciliaciones.msfilemanagement.application.port.out;

import com.conciliaciones.domain.file.SourceFile;
import com.conciliaciones.domain.processing.ProcessingStatus;

import java.util.Optional;

public interface SourceFilePersistencePort {

    SourceFile save(SourceFile sourceFile);

    Optional<SourceFile> findById(Long id);

    Optional<SourceFile> findByChecksum(String checksum);

    SourceFile updateStatus(Long sourceFileId, ProcessingStatus status, String errorMessage, String updatedBy);
}
