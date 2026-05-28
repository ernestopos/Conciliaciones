package com.conciliaciones.reconciliation.core.application.port.in.sourceFile;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.sourceFile.SourceFileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListSourceFilesUseCase {
    Page<SourceFileResponse> list(Pageable pageable);
}
