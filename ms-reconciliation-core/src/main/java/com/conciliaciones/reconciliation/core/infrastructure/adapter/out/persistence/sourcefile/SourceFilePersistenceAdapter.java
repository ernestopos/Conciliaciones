package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.sourcefile;

import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.persistence.repository.SourceFileRepository;
import com.conciliaciones.reconciliation.core.application.port.out.sourcefile.SourceFilePersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SourceFilePersistenceAdapter implements SourceFilePersistencePort {

    private final SourceFileRepository repository;

    @Override
    public Page<SourceFileEntity> findAll(Pageable pageable) {
        log.info("LOG INICIO X = findAllSourceFilePersistence");
        Page<SourceFileEntity> result = repository.findAll(pageable);
        log.info("LOG FIN X = findAllSourceFilePersistence totalElements={}", result.getTotalElements());
        return result;
    }
}
