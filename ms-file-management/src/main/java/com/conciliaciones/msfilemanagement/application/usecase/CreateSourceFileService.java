package com.conciliaciones.msfilemanagement.application.usecase;

import com.conciliaciones.domain.audit.AuditInfo;
import com.conciliaciones.domain.file.SourceFile;
import com.conciliaciones.domain.processing.ProcessingStatus;
import com.conciliaciones.msfilemanagement.application.port.in.CreateSourceFileCommand;
import com.conciliaciones.msfilemanagement.application.port.in.CreateSourceFileUseCase;
import com.conciliaciones.msfilemanagement.application.port.out.SourceFilePersistencePort;
import com.conciliaciones.msfilemanagement.infrastructure.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateSourceFileService implements CreateSourceFileUseCase {

    private final SourceFilePersistencePort sourceFilePersistencePort;

    @Override
    public SourceFile create(CreateSourceFileCommand command) {
        sourceFilePersistencePort.findByChecksum(command.checksum())
                .ifPresent(existing -> {
                    throw new BusinessException("Ya existe un archivo con el mismo checksum");
                });

        SourceFile sourceFile = SourceFile.builder()
                .clientId(command.clientId())
                .originalFileName(command.originalFileName())
                .storagePath(command.storagePath())
                .mimeType(command.mimeType())
                .fileSize(command.fileSize())
                .fileType(command.fileType())
                .checksum(command.checksum())
                .processingStatus(ProcessingStatus.PRESIGNED)
                .auditInfo(AuditInfo.builder()
                        .createdBy(command.createdBy() == null || command.createdBy().isBlank() ? "SYSTEM" : command.createdBy())
                        .createdAt(OffsetDateTime.now())
                        .modifiedBy(command.createdBy() == null || command.createdBy().isBlank() ? "SYSTEM" : command.createdBy())
                        .modifiedAt(OffsetDateTime.now())
                        .build())
                .build();

        return sourceFilePersistencePort.save(sourceFile);
    }
}
