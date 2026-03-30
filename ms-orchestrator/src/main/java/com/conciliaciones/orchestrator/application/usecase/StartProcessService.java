package com.conciliaciones.orchestrator.application.usecase;

import com.conciliaciones.orchestrator.application.port.in.StartProcessUseCase;
import com.conciliaciones.orchestrator.application.port.out.OrchestrationEventPublisherPort;
import com.conciliaciones.orchestrator.application.port.out.OrchestrationPersistencePort;
import com.conciliaciones.orchestrator.domain.model.OrchestrationProcess;
import com.conciliaciones.orchestrator.domain.model.ProcessStatus;
import com.conciliaciones.orchestrator.infrastructure.adapter.in.rest.dto.ProcessResponse;
import com.conciliaciones.orchestrator.infrastructure.adapter.in.rest.dto.StartProcessRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StartProcessService implements StartProcessUseCase {

    private final OrchestrationPersistencePort orchestrationPersistencePort;
    private final OrchestrationEventPublisherPort orchestrationEventPublisherPort;

    @Override
    public ProcessResponse start(StartProcessRequest request) {
        OrchestrationProcess saved = orchestrationPersistencePort.save(
                OrchestrationProcess.builder()
                        .processCode(request.processCode())
                        .businessCode(request.businessCode())
                        .status(ProcessStatus.CREATED)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        orchestrationEventPublisherPort.publishProcessStarted(saved.getId(), saved.getProcessCode());

        return new ProcessResponse(
                saved.getId(),
                saved.getProcessCode(),
                saved.getBusinessCode(),
                saved.getStatus().name()
        );
    }
}
