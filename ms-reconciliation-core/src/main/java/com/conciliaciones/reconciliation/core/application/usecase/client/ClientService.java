    package com.conciliaciones.reconciliation.core.application.usecase.client;

    import com.conciliaciones.domain.entity.ClientEntity;
import com.conciliaciones.reconciliation.core.application.port.in.client.CreateClientUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.client.DeleteClientUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.client.GetClientByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.client.ListClientsUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.client.UpdateClientUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.client.ClientPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client.ClientResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client.CreateClientRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client.UpdateClientRequest;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

    @Service
    @Slf4j
    @RequiredArgsConstructor
    public class ClientService implements CreateClientUseCase, GetClientByIdUseCase, ListClientsUseCase, UpdateClientUseCase, DeleteClientUseCase {

        private final ClientPersistencePort persistencePort;

        @Override
        public ClientResponse create(CreateClientRequest request, String username) {
            log.info("LOG INICIO X = createClient");

            ClientEntity entity = ClientEntity.builder()
                .externalClientId(request.externalClientId())
                .firstName(request.firstName())
                .middleName(request.middleName())
                .lastName(request.lastName())
                .fullName(request.fullName())
                .birthDate(request.birthDate())
                .state(request.state())
                .active(request.active())
                    .createdAt(LocalDateTime.now())
                    .createdBy(username)
                    .build();

            ClientEntity saved = persistencePort.save(entity);
            log.info("LOG FIN X = createClient id={}", saved.getId());
            return toResponse(saved);
        }

        @Override
        public ClientResponse getById(Long id) {
            log.info("LOG INICIO X = getClientById id={}", id);
            ClientEntity entity = persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Client no encontrado con id: " + id));
            log.info("LOG FIN X = getClientById id={}", entity.getId());
            return toResponse(entity);
        }

        @Override
        public Page<ClientResponse> list(Pageable pageable) {
            log.info("LOG INICIO X = listClients page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
            Page<ClientResponse> result = persistencePort.findAll(pageable).map(this::toResponse);
            log.info("LOG FIN X = listClients totalElements={}", result.getTotalElements());
            return result;
        }

        @Override
        public ClientResponse update(Long id, UpdateClientRequest request, String username) {
            log.info("LOG INICIO X = updateClient id={}", id);

            ClientEntity entity = persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Client no encontrado con id: " + id));

        entity.setExternalClientId(request.externalClientId());
        entity.setFirstName(request.firstName());
        entity.setMiddleName(request.middleName());
        entity.setLastName(request.lastName());
        entity.setFullName(request.fullName());
        entity.setBirthDate(request.birthDate());
        entity.setState(request.state());
        entity.setActive(request.active());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setUpdatedBy(username);

            ClientEntity saved = persistencePort.save(entity);
            log.info("LOG FIN X = updateClient id={}", saved.getId());
            return toResponse(saved);
        }

        @Override
        public void delete(Long id) {
            log.info("LOG INICIO X = deleteClient id={}", id);
            persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Client no encontrado con id: " + id));
            persistencePort.deleteById(id);
            log.info("LOG FIN X = deleteClient id={}", id);
        }

        private ClientResponse toResponse(ClientEntity entity) {
            return new ClientResponse(
                entity.getId(),
                entity.getExternalClientId(),
                entity.getFirstName(),
                entity.getMiddleName(),
                entity.getLastName(),
                entity.getFullName(),
                entity.getBirthDate(),
                entity.getState(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
            );
        }
    }
