package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest;

import com.conciliaciones.reconciliation.core.application.port.in.CreateReconciliationUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.ExecuteReconciliationUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.GetReconciliationUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.CreateReconciliationRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.ReconciliationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reconciliations")
@RequiredArgsConstructor
@Tag(name = "Reconciliations", description = "Operaciones del core de conciliación")
public class ReconciliationController {

    private final CreateReconciliationUseCase createReconciliationUseCase;
    private final GetReconciliationUseCase getReconciliationUseCase;
    private final ExecuteReconciliationUseCase executeReconciliationUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReconciliationResponse create(@Valid @RequestBody CreateReconciliationRequest request) {
        return createReconciliationUseCase.create(request);
    }

    @GetMapping("/{id}")
    public ReconciliationResponse getById(@PathVariable Long id) {
        return getReconciliationUseCase.getById(id);
    }

    @PostMapping("/{id}/execute")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void execute(@PathVariable Long id) {
        executeReconciliationUseCase.execute(id);
    }
}
