package com.conciliaciones.reconciliation.core.application.port.out.securityUser;

import com.conciliaciones.domain.entity.SecurityUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SecurityUserPersistencePort {

    SecurityUserEntity save(SecurityUserEntity entity);

    Optional<SecurityUserEntity> findByUsername(String username);

    Page<SecurityUserEntity> findAll(Pageable pageable);

    Page<SecurityUserEntity> findByActive(Boolean active, Pageable pageable);

    boolean existsByEmail(String email);
}