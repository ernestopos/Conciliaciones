package com.conciliaciones.reconciliation.core.application.port.out.sourcefile;

import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.persistence.repository.projection.DonutCharValTolProjection;
import com.conciliaciones.persistence.repository.projection.SourceFileProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SourceFilePersistencePort {
    Page<SourceFileEntity> findAll(Pageable pageable);
    Page<SourceFileProjection> findAllWithProcessingStatus(Pageable pageable);
    public List<DonutCharValTolProjection> charFileUploads();
}
