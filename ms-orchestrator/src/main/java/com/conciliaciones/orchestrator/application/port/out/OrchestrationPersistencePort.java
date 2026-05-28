package com.conciliaciones.orchestrator.application.port.out;

import com.conciliaciones.orchestrator.domain.model.OrchestrationProcess;
import java.util.Optional;

public interface OrchestrationPersistencePort {
    OrchestrationProcess save(OrchestrationProcess process);
    Optional<OrchestrationProcess> findById(Long id);
}
