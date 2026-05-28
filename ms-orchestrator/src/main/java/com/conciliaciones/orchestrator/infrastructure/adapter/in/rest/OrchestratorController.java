package com.conciliaciones.orchestrator.infrastructure.adapter.in.rest;

import com.conciliaciones.orchestrator.application.port.in.AdvanceProcessUseCase;
import com.conciliaciones.orchestrator.application.port.in.GetProcessStatusUseCase;
import com.conciliaciones.orchestrator.application.port.in.StartProcessUseCase;
import com.conciliaciones.orchestrator.infrastructure.adapter.in.rest.dto.ProcessResponse;
import com.conciliaciones.orchestrator.infrastructure.adapter.in.rest.dto.StartProcessRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orchestrations")
@RequiredArgsConstructor
@Tag(name = "Orchestrator", description = "Operaciones del orquestador")
public class OrchestratorController {

    private final StartProcessUseCase startProcessUseCase;
    private final GetProcessStatusUseCase getProcessStatusUseCase;
    private final AdvanceProcessUseCase advanceProcessUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProcessResponse start(@Valid @RequestBody StartProcessRequest request) {
        return startProcessUseCase.start(request);
    }

    @GetMapping("/{id}")
    public ProcessResponse getById(@PathVariable Long id) {
        return getProcessStatusUseCase.getById(id);
    }

    @PostMapping("/{id}/advance")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void advance(@PathVariable Long id) {
        advanceProcessUseCase.advance(id);
    }
}
