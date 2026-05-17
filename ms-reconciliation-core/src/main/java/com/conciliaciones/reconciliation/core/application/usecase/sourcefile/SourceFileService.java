package com.conciliaciones.reconciliation.core.application.usecase.sourcefile;

import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.reconciliation.core.application.port.in.ListSourceFilesUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.sourcefile.SourceFilePersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.SourceFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceFileService implements ListSourceFilesUseCase {

    private final SourceFilePersistencePort persistencePort;

    @Override
    public Page<SourceFileResponse> list(Pageable pageable) {
        log.info("LOG INICIO X = listSourceFiles page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<SourceFileResponse> result = persistencePort.findAll(pageable).map(this::toResponse);
        log.info("LOG FIN X = listSourceFiles totalElements={}", result.getTotalElements());
        return result;
    }

    private SourceFileResponse toResponse(SourceFileEntity entity) {
        return new SourceFileResponse(
                entity.getId(),
                entity.getCarrierId(),
                entity.getOriginalFileName(),
                entity.getStoredFileName(),
                entity.getFileExtension(),
                entity.getMimeType(),
                entity.getFileSizeBytes(),
                entity.getS3Bucket(),
                entity.getS3Key(),
                entity.getChecksum(),
                entity.getSourceSystem(),
                entity.getUploadDate(),
                entity.getUploadedBy(),
                entity.getProcessingStatusId(),
                entity.getErrorMessage(),
                entity.getTotalRows(),
                entity.getProcessedRows(),
                entity.getFailedRows()
        );
    }
}
