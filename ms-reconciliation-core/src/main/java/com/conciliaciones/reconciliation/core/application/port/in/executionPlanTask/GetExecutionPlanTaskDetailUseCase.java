package com.conciliaciones.reconciliation.core.application.port.in.executionPlanTask;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask.ExecutionPlanTaskDetailResponse;

public interface GetExecutionPlanTaskDetailUseCase {
    ExecutionPlanTaskDetailResponse getDetail(Long id);
}
