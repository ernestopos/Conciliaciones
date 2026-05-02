    package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.client;

    import com.conciliaciones.reconciliation.core.application.port.in.client.CreateClientUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.client.DeleteClientUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.client.GetClientByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.client.ListClientsUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.client.UpdateClientUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client.ClientResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client.CreateClientRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client.UpdateClientRequest;
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
    @RequestMapping("/api/v1/clients")
    @RequiredArgsConstructor
    @Slf4j
    @Tag(name = "Client", description = "Operaciones CRUD para Client")
    public class ClientController {

        private final CreateClientUseCase createClientUseCase;
        private final GetClientByIdUseCase getClientByIdUseCase;
        private final ListClientsUseCase listClientsUseCase;
        private final UpdateClientUseCase updateClientUseCase;
        private final DeleteClientUseCase deleteClientUseCase;

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public ClientResponse create(@Valid @RequestBody CreateClientRequest request, @AuthenticationPrincipal Jwt jwt) {
            log.info("LOG INICIO X = createClientController");
            ClientResponse response = createClientUseCase.create(request, AuthenticatedUserResolver.resolveUsername(jwt));
            log.info("LOG FIN X = createClientController id={}", response.id());
            return response;
        }

        @GetMapping("/{id}")
        public ClientResponse getById(@PathVariable Long id) {
            log.info("LOG INICIO X = getClientByIdController id={}", id);
            ClientResponse response = getClientByIdUseCase.getById(id);
            log.info("LOG FIN X = getClientByIdController id={}", response.id());
            return response;
        }

        @GetMapping
        public Page<ClientResponse> list(Pageable pageable) {
            log.info("LOG INICIO X = listClientsController page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
            Page<ClientResponse> response = listClientsUseCase.list(pageable);
            log.info("LOG FIN X = listClientsController totalElements={}", response.getTotalElements());
            return response;
        }

        @PutMapping("/{id}")
        public ClientResponse update(@PathVariable Long id, @Valid @RequestBody UpdateClientRequest request, @AuthenticationPrincipal Jwt jwt) {
            log.info("LOG INICIO X = updateClientController id={}", id);
            ClientResponse response = updateClientUseCase.update(id, request, AuthenticatedUserResolver.resolveUsername(jwt));
            log.info("LOG FIN X = updateClientController id={}", response.id());
            return response;
        }

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void delete(@PathVariable Long id) {
            log.info("LOG INICIO X = deleteClientController id={}", id);
            deleteClientUseCase.delete(id);
            log.info("LOG FIN X = deleteClientController id={}", id);
        }

    }
