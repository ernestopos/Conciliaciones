package com.conciliaciones.orchestrator.application.port.in;

import com.conciliaciones.orchestrator.infrastructure.adapter.in.rest.dto.ProcessResponse;

public interface GetProcessStatusUseCase {
    ProcessResponse getById(Long id);
}
