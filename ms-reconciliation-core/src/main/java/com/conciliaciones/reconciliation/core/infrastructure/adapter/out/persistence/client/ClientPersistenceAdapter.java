package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.client;

import com.conciliaciones.domain.entity.ClientEntity;
import com.conciliaciones.persistence.repository.ClientRepository;
import com.conciliaciones.reconciliation.core.application.port.out.client.ClientPersistencePort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClientPersistenceAdapter implements ClientPersistencePort {

    private final ClientRepository repository;

    @Override
    public ClientEntity save(ClientEntity entity) {
        log.info("LOG INICIO X = saveClientPersistence");
        ClientEntity saved = repository.save(entity);
        repository.flush();
        log.info("LOG FIN X = saveClientPersistence id={}", saved.getId());
        return saved;
    }

    @Override
    public Optional<ClientEntity> findById(Long id) {
        log.info("LOG INICIO X = findClientByIdPersistence id={}", id);
        Optional<ClientEntity> result = repository.findById(id);
        log.info("LOG FIN X = findClientByIdPersistence found={}", result.isPresent());
        return result;
    }

    @Override
    public Page<ClientEntity> findAll(Pageable pageable) {
        log.info("LOG INICIO X = findAllClientPersistence");
        Page<ClientEntity> result = repository.findAll(pageable);
        log.info("LOG FIN X = findAllClientPersistence totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public Page<ClientEntity> search(String externalClientId, String fullName, Pageable pageable) {
        String externalClientIdFilter = normalizeFilter(externalClientId);
        String fullNameFilter = normalizeFilter(fullName);

        if (externalClientIdFilter == null && fullNameFilter == null) {
            return repository.findByActiveTrue(pageable);
        }

        if (externalClientIdFilter != null && fullNameFilter == null) {
            return repository.findByActiveTrueAndExternalClientIdContainingIgnoreCase(
                    externalClientIdFilter,
                    pageable
            );
        }

        if (externalClientIdFilter == null) {
            return repository.findByActiveTrueAndFullNameContainingIgnoreCase(
                    fullNameFilter,
                    pageable
            );
        }

        return repository.findByActiveTrueAndExternalClientIdContainingIgnoreCaseOrActiveTrueAndFullNameContainingIgnoreCase(
                externalClientIdFilter,
                fullNameFilter,
                pageable
        );
    }

    private String normalizeFilter(String value) {
        if (value == null || value.trim().isBlank()) {
            return null;
        }
        return value.trim();
    }

    @Override
    public void deleteById(Long id) {
        log.info("LOG INICIO X = deleteClientPersistence id={}", id);
        repository.deleteById(id);
        log.info("LOG FIN X = deleteClientPersistence id={}", id);
    }
}
