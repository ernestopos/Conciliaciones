package com.conciliaciones.orchestrator.application.usecase;

import com.conciliaciones.orchestrator.application.port.in.GetProcessStatusUseCase;
import com.conciliaciones.orchestrator.application.port.out.OrchestrationPersistencePort;
import com.conciliaciones.orchestrator.infrastructure.adapter.in.rest.dto.ProcessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetProcessStatusService implements GetProcessStatusUseCase {

    private final OrchestrationPersistencePort orchestrationPersistencePort;

    @Override
    public ProcessResponse getById(Long id) {
        var process = orchestrationPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado: " + id));

        return new ProcessResponse(
                process.getId(),
                process.getProcessCode(),
                process.getBusinessCode(),
                process.getStatus().name()
        );
    }
}
