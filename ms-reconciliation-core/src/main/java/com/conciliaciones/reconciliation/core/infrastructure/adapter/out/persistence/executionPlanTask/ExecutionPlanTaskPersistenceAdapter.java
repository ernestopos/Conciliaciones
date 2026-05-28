package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.executionPlanTask;

import com.conciliaciones.persistence.jpa.entity.ExecutionPlanTaskEntity;
import com.conciliaciones.persistence.repository.ExecutionPlanTaskRepository;
import com.conciliaciones.persistence.repository.SourceFileValidationRepository;
import com.conciliaciones.persistence.repository.projection.SourceFileValidationDetailProjection;
import com.conciliaciones.reconciliation.core.application.port.out.executionPlanTask.ExecutionPlanTaskPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExecutionPlanTaskPersistenceAdapter implements ExecutionPlanTaskPersistencePort {

    private final ExecutionPlanTaskRepository repository;
    private final SourceFileValidationRepository sourceFileValidationRepository;

    @Override
    public Page<ExecutionPlanTaskEntity> findAll(Pageable pageable) {
        log.info("LOG INICIO X = findAllExecutionPlanTaskPersistence page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ExecutionPlanTaskEntity> result = repository.findAll(pageable);
        log.info("LOG FIN X = findAllExecutionPlanTaskPersistence totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public Optional<ExecutionPlanTaskEntity> findByIdWithScheduledTasks(Long id) {
        log.info("LOG INICIO X = findExecutionPlanTaskByIdWithScheduledTasksPersistence id={}", id);
        Optional<ExecutionPlanTaskEntity> result = repository.findByIdWithScheduledTasks(id);
        log.info("LOG FIN X = findExecutionPlanTaskByIdWithScheduledTasksPersistence found={}", result.isPresent());
        return result;
    }

    @Override
    public List<SourceFileValidationDetailProjection> findValidationDetailByExecutionPlanTaskId(Long executionPlanTaskId) {
        log.info("LOG INICIO X = findValidationDetailByExecutionPlanTaskIdPersistence executionPlanTaskId={}", executionPlanTaskId);
        List<SourceFileValidationDetailProjection> result = sourceFileValidationRepository
                .findValidationDetailByExecutionPlanTaskId(executionPlanTaskId);
        log.info("LOG FIN X = findValidationDetailByExecutionPlanTaskIdPersistence total={}", result.size());
        return result;
    }
}
