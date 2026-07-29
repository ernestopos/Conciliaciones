package com.conciliaciones.reconciliation.core.application.usecase.sourcefile;

import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.persistence.repository.projection.DonutCharValTolProjection;
import com.conciliaciones.persistence.repository.projection.SourceFileProjection;
import com.conciliaciones.reconciliation.core.application.port.in.sourceFile.ListSourceFilesUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.sourcefile.SourceFilePersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.sourceFile.SourceFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceFileService implements ListSourceFilesUseCase {

    private final SourceFilePersistencePort persistencePort;

    @Override
    @Transactional(readOnly = true)
    public Page<SourceFileResponse> list(Pageable pageable) {
        log.info("LOG INICIO X = listSourceFiles page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<SourceFileResponse> result = persistencePort.findAll(pageable).map(this::toResponse);
        log.info("LOG FIN X = listSourceFiles totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SourceFileProjection> findAllWithProcessingStatus(Pageable pageable) {
        log.info("LOG INICIO X = findAllWithProcessingStatus page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<SourceFileProjection> result = persistencePort.findAllWithProcessingStatus(pageable);
        log.info("LOG FIN X = findAllWithProcessingStatus totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonutCharValTolProjection> charFileUploads() {
        log.info("LOG INICIO X = charFileUploads page={} size={}");
        List<DonutCharValTolProjection> result = persistencePort.charFileUploads();
        log.info("LOG FIN X = charFileUploads totalElements={}", result.size());
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
