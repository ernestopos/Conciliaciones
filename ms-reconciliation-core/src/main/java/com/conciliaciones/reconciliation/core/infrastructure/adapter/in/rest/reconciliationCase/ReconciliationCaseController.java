    package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.reconciliationCase;

    import com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase.CreateReconciliationCaseUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase.DeleteReconciliationCaseUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase.GetReconciliationCaseByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase.ListReconciliationCasesUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase.ReprocessReconciliationCaseUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase.UpdateReconciliationCaseUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.CreateReconciliationCaseRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.ReconciliationCaseResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.UpdateReconciliationCaseRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.support.AuthenticatedUserResolver;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/v1/reconciliation-cases")
    @RequiredArgsConstructor
    @Slf4j
    @Tag(name = "ReconciliationCase", description = "Operaciones CRUD para ReconciliationCase")
    public class ReconciliationCaseController {

        private final CreateReconciliationCaseUseCase createReconciliationCaseUseCase;
        private final GetReconciliationCaseByIdUseCase getReconciliationCaseByIdUseCase;
        private final ListReconciliationCasesUseCase listReconciliationCasesUseCase;
        private final UpdateReconciliationCaseUseCase updateReconciliationCaseUseCase;
        private final DeleteReconciliationCaseUseCase deleteReconciliationCaseUseCase;
    private final ReprocessReconciliationCaseUseCase reprocessReconciliationCaseUseCase;

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public ReconciliationCaseResponse create(@Valid @RequestBody CreateReconciliationCaseRequest request, @AuthenticationPrincipal Jwt jwt) {
            log.info("LOG INICIO X = createReconciliationCaseController");
            ReconciliationCaseResponse response = createReconciliationCaseUseCase.create(request, AuthenticatedUserResolver.resolveUsername(jwt));
            log.info("LOG FIN X = createReconciliationCaseController id={}", response.id());
            return response;
        }

        @GetMapping("/{id}")
        public ReconciliationCaseResponse getById(@PathVariable Long id) {
            log.info("LOG INICIO X = getReconciliationCaseByIdController id={}", id);
            ReconciliationCaseResponse response = getReconciliationCaseByIdUseCase.getById(id);
            log.info("LOG FIN X = getReconciliationCaseByIdController id={}", response.id());
            return response;
        }

        @GetMapping
        public Page<ReconciliationCaseResponse> list(Pageable pageable) {
            log.info("LOG INICIO X = listReconciliationCasesController page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
            Page<ReconciliationCaseResponse> response = listReconciliationCasesUseCase.list(pageable);
            log.info("LOG FIN X = listReconciliationCasesController totalElements={}", response.getTotalElements());
            return response;
        }

        @PutMapping("/{id}")
        public ReconciliationCaseResponse update(@PathVariable Long id, @Valid @RequestBody UpdateReconciliationCaseRequest request, @AuthenticationPrincipal Jwt jwt) {
            log.info("LOG INICIO X = updateReconciliationCaseController id={}", id);
            ReconciliationCaseResponse response = updateReconciliationCaseUseCase.update(id, request, AuthenticatedUserResolver.resolveUsername(jwt));
            log.info("LOG FIN X = updateReconciliationCaseController id={}", response.id());
            return response;
        }

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void delete(@PathVariable Long id) {
            log.info("LOG INICIO X = deleteReconciliationCaseController id={}", id);
            deleteReconciliationCaseUseCase.delete(id);
            log.info("LOG FIN X = deleteReconciliationCaseController id={}", id);
        }

@PostMapping("/{id}/reprocess")
public ReconciliationCaseResponse reprocess(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    log.info("LOG INICIO X = reprocessReconciliationCaseController id={}", id);
    ReconciliationCaseResponse response = reprocessReconciliationCaseUseCase.reprocess(id, AuthenticatedUserResolver.resolveUsername(jwt));
    log.info("LOG FIN X = reprocessReconciliationCaseController id={}", response.id());
    return response;
}

    }
