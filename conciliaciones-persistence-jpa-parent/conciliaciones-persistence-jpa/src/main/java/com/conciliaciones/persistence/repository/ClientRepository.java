package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    Page<ClientEntity> findByActiveTrue(Pageable pageable);

    Page<ClientEntity> findByActiveTrueAndExternalClientIdContainingIgnoreCase(
            String externalClientId,
            Pageable pageable
    );

    Page<ClientEntity> findByActiveTrueAndFullNameContainingIgnoreCase(
            String fullName,
            Pageable pageable
    );

    Page<ClientEntity> findByActiveTrueAndExternalClientIdContainingIgnoreCaseOrActiveTrueAndFullNameContainingIgnoreCase(
            String externalClientId,
            String fullName,
            Pageable pageable
    );
}