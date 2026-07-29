package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.sourcefile;

import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.persistence.repository.SourceFileRepository;
import com.conciliaciones.persistence.repository.projection.DonutCharValTolProjection;
import com.conciliaciones.persistence.repository.projection.SourceFileProjection;
import com.conciliaciones.reconciliation.core.application.port.out.sourcefile.SourceFilePersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SourceFilePersistenceAdapter implements SourceFilePersistencePort {

    private final SourceFileRepository repository;

    @Override
    public Page<SourceFileEntity> findAll(Pageable pageable) {
        log.info("LOG INICIO X = findAllSourceFilePersistence page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<SourceFileEntity> result = repository.findAll(pageable);
        log.info("LOG FIN X = findAllSourceFilePersistence totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public Page<SourceFileProjection> findAllWithProcessingStatus(Pageable pageable) {
        log.info("LOG INICIO X = findAllWithProcessingStatus page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<SourceFileProjection> result = repository.findAllWithProcessingStatus(pageable);
        log.info("LOG FIN X = findAllWithProcessingStatus totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public List<DonutCharValTolProjection> charFileUploads() {
        log.info("LOG INICIO X = charFileUploads page={} size={}");
        List<DonutCharValTolProjection> result = repository.charFileUploads();
        log.info("LOG FIN X = findAllWithProcessingStatus totalElements={}", result.size());
        return result;
    }
}
