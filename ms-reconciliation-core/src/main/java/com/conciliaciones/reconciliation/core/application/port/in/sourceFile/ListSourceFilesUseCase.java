package com.conciliaciones.reconciliation.core.application.port.in.sourceFile;

import com.conciliaciones.persistence.repository.projection.DonutCharValTolProjection;
import com.conciliaciones.persistence.repository.projection.SourceFileProjection;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.sourceFile.SourceFileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListSourceFilesUseCase {
    Page<SourceFileResponse> list(Pageable pageable);
    Page<SourceFileProjection> findAllWithProcessingStatus(Pageable pageable);
    List<DonutCharValTolProjection> charFileUploads();
}
