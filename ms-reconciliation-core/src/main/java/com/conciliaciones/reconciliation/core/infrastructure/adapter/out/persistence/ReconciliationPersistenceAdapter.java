package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence;

import com.conciliaciones.reconciliation.core.application.port.out.ReconciliationPersistencePort;
import com.conciliaciones.reconciliation.core.domain.model.Reconciliation;
import com.conciliaciones.reconciliation.core.domain.model.ReconciliationStatus;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

/**
 * Cascarón base.
 * Aquí se conectará luego con conciliaciones-persistence-jpa
 * usando los repositorios/entidades centralizados.
 */
@Component
public class ReconciliationPersistenceAdapter implements ReconciliationPersistencePort {

    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<Long, Reconciliation> store = new ConcurrentHashMap<>();

    @Override
    public Reconciliation save(Reconciliation reconciliation) {
        Long id = reconciliation.getId() != null ? reconciliation.getId() : sequence.incrementAndGet();

        Reconciliation saved = Reconciliation.builder()
                .id(id)
                .businessCode(reconciliation.getBusinessCode())
                .sourceFileName(reconciliation.getSourceFileName())
                .status(reconciliation.getStatus() != null ? reconciliation.getStatus() : ReconciliationStatus.CREATED)
                .createdAt(reconciliation.getCreatedAt())
                .build();

        store.put(id, saved);
        return saved;
    }

    @Override
    public Optional<Reconciliation> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}
