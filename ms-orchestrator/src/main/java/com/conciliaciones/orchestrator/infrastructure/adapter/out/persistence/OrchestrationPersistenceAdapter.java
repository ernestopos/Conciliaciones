package com.conciliaciones.orchestrator.infrastructure.adapter.out.persistence;

import com.conciliaciones.orchestrator.application.port.out.OrchestrationPersistencePort;
import com.conciliaciones.orchestrator.domain.model.OrchestrationProcess;
import com.conciliaciones.orchestrator.domain.model.ProcessStatus;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

/**
 * Cascarón base.
 * Luego se conectará con conciliaciones-persistence-jpa.
 */
@Component
public class OrchestrationPersistenceAdapter implements OrchestrationPersistencePort {

    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<Long, OrchestrationProcess> store = new ConcurrentHashMap<>();

    @Override
    public OrchestrationProcess save(OrchestrationProcess process) {
        Long id = process.getId() != null ? process.getId() : sequence.incrementAndGet();

        OrchestrationProcess saved = OrchestrationProcess.builder()
                .id(id)
                .processCode(process.getProcessCode())
                .businessCode(process.getBusinessCode())
                .status(process.getStatus() != null ? process.getStatus() : ProcessStatus.CREATED)
                .createdAt(process.getCreatedAt())
                .build();

        store.put(id, saved);
        return saved;
    }

    @Override
    public Optional<OrchestrationProcess> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}
