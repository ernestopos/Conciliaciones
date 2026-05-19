package com.conciliaciones.reconciliation.core.application.port.in.executionPlanTask;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask.ExecutionPlanTaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListExecutionPlanTasksUseCase {
    Page<ExecutionPlanTaskResponse> list(Pageable pageable);
}
