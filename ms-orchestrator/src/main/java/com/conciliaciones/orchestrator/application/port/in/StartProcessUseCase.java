package com.conciliaciones.orchestrator.application.port.in;

import com.conciliaciones.orchestrator.infrastructure.adapter.in.rest.dto.ProcessResponse;
import com.conciliaciones.orchestrator.infrastructure.adapter.in.rest.dto.StartProcessRequest;

public interface StartProcessUseCase {
    ProcessResponse start(StartProcessRequest request);
}
