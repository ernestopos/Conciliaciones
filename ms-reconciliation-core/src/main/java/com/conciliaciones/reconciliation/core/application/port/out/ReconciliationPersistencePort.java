package com.conciliaciones.reconciliation.core.application.port.out;

import com.conciliaciones.reconciliation.core.domain.model.Reconciliation;
import java.util.Optional;

public interface ReconciliationPersistencePort {
    Reconciliation save(Reconciliation reconciliation);
    Optional<Reconciliation> findById(Long id);
}
