package com.conciliaciones.reconciliation.core.application.port.out.sourcefile;

import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SourceFilePersistencePort {
    Page<SourceFileEntity> findAll(Pageable pageable);
}
