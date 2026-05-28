package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.executionPlanTask;

import com.conciliaciones.reconciliation.core.application.port.in.executionPlanTask.GetExecutionPlanTaskDetailUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.executionPlanTask.GetValidationExecutionDetailUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.executionPlanTask.ListExecutionPlanTasksUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask.ExecutionPlanTaskDetailResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask.ExecutionPlanTaskResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask.ValidationExecutionDetailResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/execution-plan-tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Execution Plan Tasks", description = "Consulta de planes de tareas ejecutados")
public class ExecutionPlanTaskController {

    private final ListExecutionPlanTasksUseCase listExecutionPlanTasksUseCase;
    private final GetExecutionPlanTaskDetailUseCase getExecutionPlanTaskDetailUseCase;
    private final GetValidationExecutionDetailUseCase getValidationExecutionDetailUseCase;

    @GetMapping
    public Page<ExecutionPlanTaskResponse> list(Pageable pageable) {
        log.info("LOG INICIO X = listExecutionPlanTasksController page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ExecutionPlanTaskResponse> response = listExecutionPlanTasksUseCase.list(pageable);
        log.info("LOG FIN X = listExecutionPlanTasksController totalElements={}", response.getTotalElements());
        return response;
    }

    @GetMapping("/{id}")
    public ExecutionPlanTaskDetailResponse getDetail(@PathVariable Long id) {
        log.info("LOG INICIO X = getExecutionPlanTaskDetailController id={}", id);
        ExecutionPlanTaskDetailResponse response = getExecutionPlanTaskDetailUseCase.getDetail(id);
        log.info("LOG FIN X = getExecutionPlanTaskDetailController id={}", response.id());
        return response;
    }

    @GetMapping("/{id}/validation-details")
    public List<ValidationExecutionDetailResponse> getValidationDetails(@PathVariable Long id) {
        log.info("LOG INICIO X = getValidationExecutionDetailController executionPlanTaskId={}", id);
        List<ValidationExecutionDetailResponse> response = getValidationExecutionDetailUseCase.getValidationDetail(id);
        log.info("LOG FIN X = getValidationExecutionDetailController executionPlanTaskId={} total={}", id, response.size());
        return response;
    }
}
