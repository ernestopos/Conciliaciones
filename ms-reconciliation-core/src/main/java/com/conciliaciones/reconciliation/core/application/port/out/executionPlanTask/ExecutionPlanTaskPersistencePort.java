package com.conciliaciones.reconciliation.core.application.port.out.executionPlanTask;

import com.conciliaciones.persistence.jpa.entity.ExecutionPlanTaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ExecutionPlanTaskPersistencePort {
    Page<ExecutionPlanTaskEntity> findAll(Pageable pageable);
    Optional<ExecutionPlanTaskEntity> findByIdWithScheduledTasks(Long id);
}
