    package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.producer;

    import com.conciliaciones.reconciliation.core.application.port.in.producer.CreateProducerUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.producer.DeleteProducerUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.producer.GetProducerByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.producer.ListProducersUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.producer.UpdateProducerUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer.CreateProducerRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer.ProducerResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer.UpdateProducerRequest;
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
    @RequestMapping("/api/v1/producers")
    @RequiredArgsConstructor
    @Slf4j
    @Tag(name = "Producer", description = "Operaciones CRUD para Producer")
    public class ProducerController {

        private final CreateProducerUseCase createProducerUseCase;
        private final GetProducerByIdUseCase getProducerByIdUseCase;
        private final ListProducersUseCase listProducersUseCase;
        private final UpdateProducerUseCase updateProducerUseCase;
        private final DeleteProducerUseCase deleteProducerUseCase;

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public ProducerResponse create(@Valid @RequestBody CreateProducerRequest request, @AuthenticationPrincipal Jwt jwt) {
            log.info("LOG INICIO X = createProducerController");
            ProducerResponse response = createProducerUseCase.create(request, AuthenticatedUserResolver.resolveUsername(jwt));
            log.info("LOG FIN X = createProducerController id={}", response.id());
            return response;
        }

        @GetMapping("/{id}")
        public ProducerResponse getById(@PathVariable Long id) {
            log.info("LOG INICIO X = getProducerByIdController id={}", id);
            ProducerResponse response = getProducerByIdUseCase.getById(id);
            log.info("LOG FIN X = getProducerByIdController id={}", response.id());
            return response;
        }

        @GetMapping
        public Page<ProducerResponse> list(Pageable pageable) {
            log.info("LOG INICIO X = listProducersController page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
            Page<ProducerResponse> response = listProducersUseCase.list(pageable);
            log.info("LOG FIN X = listProducersController totalElements={}", response.getTotalElements());
            return response;
        }

        @PutMapping("/{id}")
        public ProducerResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProducerRequest request, @AuthenticationPrincipal Jwt jwt) {
            log.info("LOG INICIO X = updateProducerController id={}", id);
            ProducerResponse response = updateProducerUseCase.update(id, request, AuthenticatedUserResolver.resolveUsername(jwt));
            log.info("LOG FIN X = updateProducerController id={}", response.id());
            return response;
        }

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void delete(@PathVariable Long id) {
            log.info("LOG INICIO X = deleteProducerController id={}", id);
            deleteProducerUseCase.delete(id);
            log.info("LOG FIN X = deleteProducerController id={}", id);
        }

    }
