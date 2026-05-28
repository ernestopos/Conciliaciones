package com.conciliaciones.reconciliation.core.application.usecase.executionPlanTask;

import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.persistence.jpa.entity.ExecutionPlanTaskEntity;
import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.reconciliation.core.application.port.in.executionPlanTask.GetExecutionPlanTaskDetailUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.executionPlanTask.GetValidationExecutionDetailUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.executionPlanTask.ListExecutionPlanTasksUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.executionPlanTask.ExecutionPlanTaskPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask.ExecutionPlanTaskDetailResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask.ExecutionPlanTaskResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask.ScheduledTaskStepResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask.ValidationExecutionDetailResponse;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExecutionPlanTaskService implements ListExecutionPlanTasksUseCase, GetExecutionPlanTaskDetailUseCase, GetValidationExecutionDetailUseCase {

    private final ExecutionPlanTaskPersistencePort persistencePort;

    @Override
    @Transactional(readOnly = true)
    public Page<ExecutionPlanTaskResponse> list(Pageable pageable) {
        log.info("LOG INICIO X = listExecutionPlanTasks page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ExecutionPlanTaskResponse> result = persistencePort.findAll(pageable).map(this::toResponse);
        log.info("LOG FIN X = listExecutionPlanTasks totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ExecutionPlanTaskDetailResponse getDetail(Long id) {
        log.info("LOG INICIO X = getExecutionPlanTaskDetail id={}", id);
        ExecutionPlanTaskEntity entity = persistencePort.findByIdWithScheduledTasks(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de ejecución no encontrado con id: " + id));
        ExecutionPlanTaskDetailResponse response = toDetailResponse(entity);
        log.info("LOG FIN X = getExecutionPlanTaskDetail id={} totalTasks={}", id, response.tasks().size());
        return response;
    }



    @Override
    @Transactional(readOnly = true)
    public List<ValidationExecutionDetailResponse> getValidationDetail(Long executionPlanTaskId) {
        log.info("LOG INICIO X = getValidationExecutionDetail executionPlanTaskId={}", executionPlanTaskId);
        if (persistencePort.findByIdWithScheduledTasks(executionPlanTaskId).isEmpty()) {
            throw new ResourceNotFoundException("Plan de ejecución no encontrado con id: " + executionPlanTaskId);
        }
        List<ValidationExecutionDetailResponse> response = persistencePort
                .findValidationDetailByExecutionPlanTaskId(executionPlanTaskId)
                .stream()
                .map(item -> new ValidationExecutionDetailResponse(
                        item.getStartedAt(),
                        item.getFinishedAt(),
                        item.getSuccessful(),
                        item.getMessage(),
                        item.getValidationTypeDescription(),
                        item.getValidationStatusDescription()
                ))
                .toList();
        log.info("LOG FIN X = getValidationExecutionDetail executionPlanTaskId={} total={}", executionPlanTaskId, response.size());
        return response;
    }

    private ExecutionPlanTaskResponse toResponse(ExecutionPlanTaskEntity entity) {
        SourceFileEntity sourceFile = entity.getSourceFile();
        ParameterEntity status = entity.getStatus();
        return new ExecutionPlanTaskResponse(
                entity.getId(),
                sourceFile != null ? sourceFile.getId() : null,
                sourceFile != null ? sourceFile.getOriginalFileName() : null,
                status != null ? status.getId() : null,
                status != null ? status.getName() : null,
                entity.getPlanExecuteCode(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getSuccessful(),
                entity.getMessage()
        );
    }

    private ExecutionPlanTaskDetailResponse toDetailResponse(ExecutionPlanTaskEntity entity) {
        ExecutionPlanTaskResponse summary = toResponse(entity);
        List<ScheduledTaskStepResponse> tasks = entity.getScheduledTasks().stream()
                .sorted(Comparator.comparing(ScheduledTaskEntity::getTaskOrder, Comparator.nullsLast(Integer::compareTo)))
                .map(this::toStepResponse)
                .toList();
        return new ExecutionPlanTaskDetailResponse(
                summary.id(), summary.sourceFileId(), summary.originalFileName(), summary.statusId(), summary.statusName(),
                summary.planExecuteCode(), summary.startedAt(), summary.finishedAt(), summary.successful(), summary.message(), tasks
        );
    }

    private ScheduledTaskStepResponse toStepResponse(ScheduledTaskEntity entity) {
        ParameterEntity taskType = entity.getTaskType();
        ParameterEntity status = entity.getStatus();
        return new ScheduledTaskStepResponse(
                entity.getId(),
                taskType != null ? taskType.getId() : null,
                taskType != null ? taskType.getName() : null,
                status != null ? status.getId() : null,
                status != null ? status.getName() : null,
                entity.getTaskOrder(),
                entity.getCronExpression(),
                entity.getTaskBeanName(),
                entity.getMethodName(),
                entity.getPayload(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
