package com.conciliaciones.reconciliation.core.application.port.in.executionPlanTask;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.executionPlanTask.ValidationExecutionDetailResponse;

import java.util.List;

public interface GetValidationExecutionDetailUseCase {

    List<ValidationExecutionDetailResponse> getValidationDetail(Long executionPlanTaskId);
}
