package com.conciliaciones.reconciliation.core.application.port.out.client;

import com.conciliaciones.domain.entity.ClientEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientPersistencePort {
    ClientEntity save(ClientEntity entity);
    Optional<ClientEntity> findById(Long id);
    Page<ClientEntity> findAll(Pageable pageable);
    void deleteById(Long id);
}
