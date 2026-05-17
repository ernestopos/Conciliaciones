package com.conciliaciones.reconciliation.core.application.port.in;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.SourceFileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListSourceFilesUseCase {
    Page<SourceFileResponse> list(Pageable pageable);
}
