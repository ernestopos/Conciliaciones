package com.conciliaciones.reconciliation.core.application.port.out.reconciliationCase;

import com.conciliaciones.domain.entity.ReconciliationCaseEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReconciliationCasePersistencePort {
    ReconciliationCaseEntity save(ReconciliationCaseEntity entity);
    Optional<ReconciliationCaseEntity> findById(Long id);
    Page<ReconciliationCaseEntity> findAll(Pageable pageable);
    void deleteById(Long id);
}
