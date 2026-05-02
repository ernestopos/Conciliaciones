package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.reconciliationCase;

import com.conciliaciones.domain.entity.ReconciliationCaseEntity;
import com.conciliaciones.persistence.repository.ReconciliationCaseRepository;
import com.conciliaciones.reconciliation.core.application.port.out.reconciliationCase.ReconciliationCasePersistencePort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReconciliationCasePersistenceAdapter implements ReconciliationCasePersistencePort {

    private final ReconciliationCaseRepository repository;

    @Override
    public ReconciliationCaseEntity save(ReconciliationCaseEntity entity) {
        log.info("LOG INICIO X = saveReconciliationCasePersistence");
        ReconciliationCaseEntity saved = repository.save(entity);
        log.info("LOG FIN X = saveReconciliationCasePersistence id={}", saved.getId());
        return saved;
    }

    @Override
    public Optional<ReconciliationCaseEntity> findById(Long id) {
        log.info("LOG INICIO X = findReconciliationCaseByIdPersistence id={}", id);
        Optional<ReconciliationCaseEntity> result = repository.findById(id);
        log.info("LOG FIN X = findReconciliationCaseByIdPersistence found={}", result.isPresent());
        return result;
    }

    @Override
    public Page<ReconciliationCaseEntity> findAll(Pageable pageable) {
        log.info("LOG INICIO X = findAllReconciliationCasePersistence");
        Page<ReconciliationCaseEntity> result = repository.findAll(pageable);
        log.info("LOG FIN X = findAllReconciliationCasePersistence totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public void deleteById(Long id) {
        log.info("LOG INICIO X = deleteReconciliationCasePersistence id={}", id);
        repository.deleteById(id);
        log.info("LOG FIN X = deleteReconciliationCasePersistence id={}", id);
    }
}
