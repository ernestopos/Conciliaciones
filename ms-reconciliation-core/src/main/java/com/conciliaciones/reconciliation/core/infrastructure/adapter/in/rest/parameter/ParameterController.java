    package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.parameter;

    import com.conciliaciones.reconciliation.core.application.port.in.parameter.CreateParameterUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.parameter.DeleteParameterUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.parameter.GetParameterByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.parameter.ListParametersUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.parameter.UpdateParameterUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.CreateParameterRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.ParameterResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.UpdateParameterRequest;
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
    @RequestMapping("/api/v1/parameters")
    @RequiredArgsConstructor
    @Slf4j
    @Tag(name = "Parameter", description = "Operaciones CRUD para Parameter")
    public class ParameterController {

        private final CreateParameterUseCase createParameterUseCase;
        private final GetParameterByIdUseCase getParameterByIdUseCase;
        private final ListParametersUseCase listParametersUseCase;
        private final UpdateParameterUseCase updateParameterUseCase;
        private final DeleteParameterUseCase deleteParameterUseCase;

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public ParameterResponse create(@Valid @RequestBody CreateParameterRequest request, @AuthenticationPrincipal Jwt jwt) {
            log.info("LOG INICIO X = createParameterController");
            ParameterResponse response = createParameterUseCase.create(request, AuthenticatedUserResolver.resolveUsername(jwt));
            log.info("LOG FIN X = createParameterController id={}", response.id());
            return response;
        }

        @GetMapping("/{id}")
        public ParameterResponse getById(@PathVariable Long id) {
            log.info("LOG INICIO X = getParameterByIdController id={}", id);
            ParameterResponse response = getParameterByIdUseCase.getById(id);
            log.info("LOG FIN X = getParameterByIdController id={}", response.id());
            return response;
        }

        @GetMapping
        public Page<ParameterResponse> list(Pageable pageable) {
            log.info("LOG INICIO X = listParametersController page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
            Page<ParameterResponse> response = listParametersUseCase.list(pageable);
            log.info("LOG FIN X = listParametersController totalElements={}", response.getTotalElements());
            return response;
        }

        @PutMapping("/{id}")
        public ParameterResponse update(@PathVariable Long id, @Valid @RequestBody UpdateParameterRequest request, @AuthenticationPrincipal Jwt jwt) {
            log.info("LOG INICIO X = updateParameterController id={}", id);
            ParameterResponse response = updateParameterUseCase.update(id, request, AuthenticatedUserResolver.resolveUsername(jwt));
            log.info("LOG FIN X = updateParameterController id={}", response.id());
            return response;
        }

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void delete(@PathVariable Long id) {
            log.info("LOG INICIO X = deleteParameterController id={}", id);
            deleteParameterUseCase.delete(id);
            log.info("LOG FIN X = deleteParameterController id={}", id);
        }

    }
