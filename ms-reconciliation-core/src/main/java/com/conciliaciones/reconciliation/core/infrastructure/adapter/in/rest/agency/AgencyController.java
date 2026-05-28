    package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.agency;

    import com.conciliaciones.reconciliation.core.application.port.in.agency.CreateAgencyUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.agency.DeleteAgencyUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.agency.GetAgencyByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.agency.ListAgenciesUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.agency.UpdateAgencyUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency.AgencyResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency.CreateAgencyRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency.UpdateAgencyRequest;
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
    @RequestMapping("/api/v1/agencies")
    @RequiredArgsConstructor
    @Slf4j
    @Tag(name = "Agency", description = "Operaciones CRUD para Agency")
    public class AgencyController {

        private final CreateAgencyUseCase createAgencyUseCase;
        private final GetAgencyByIdUseCase getAgencyByIdUseCase;
        private final ListAgenciesUseCase listAgenciesUseCase;
        private final UpdateAgencyUseCase updateAgencyUseCase;
        private final DeleteAgencyUseCase deleteAgencyUseCase;

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public AgencyResponse create(@Valid @RequestBody CreateAgencyRequest request, @AuthenticationPrincipal Jwt jwt) {
            log.info("LOG INICIO X = createAgencyController");
            AgencyResponse response = createAgencyUseCase.create(request, AuthenticatedUserResolver.resolveUsername(jwt));
            log.info("LOG FIN X = createAgencyController id={}", response.id());
            return response;
        }

        @GetMapping("/{id}")
        public AgencyResponse getById(@PathVariable Long id) {
            log.info("LOG INICIO X = getAgencyByIdController id={}", id);
            AgencyResponse response = getAgencyByIdUseCase.getById(id);
            log.info("LOG FIN X = getAgencyByIdController id={}", response.id());
            return response;
        }

        @GetMapping
        public Page<AgencyResponse> list(Pageable pageable) {
            log.info("LOG INICIO X = listAgenciesController page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
            Page<AgencyResponse> response = listAgenciesUseCase.list(pageable);
            log.info("LOG FIN X = listAgenciesController totalElements={}", response.getTotalElements());
            return response;
        }

        @PutMapping("/{id}")
        public AgencyResponse update(@PathVariable Long id, @Valid @RequestBody UpdateAgencyRequest request, @AuthenticationPrincipal Jwt jwt) {
            log.info("LOG INICIO X = updateAgencyController id={}", id);
            AgencyResponse response = updateAgencyUseCase.update(id, request, AuthenticatedUserResolver.resolveUsername(jwt));
            log.info("LOG FIN X = updateAgencyController id={}", response.id());
            return response;
        }

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void delete(@PathVariable Long id) {
            log.info("LOG INICIO X = deleteAgencyController id={}", id);
            deleteAgencyUseCase.delete(id);
            log.info("LOG FIN X = deleteAgencyController id={}", id);
        }

    }
