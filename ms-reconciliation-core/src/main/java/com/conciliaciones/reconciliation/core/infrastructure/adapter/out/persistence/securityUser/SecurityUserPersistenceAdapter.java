package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.securityUser;

import com.conciliaciones.domain.entity.SecurityUserEntity;
import com.conciliaciones.persistence.repository.SecurityUserRepository;
import com.conciliaciones.reconciliation.core.application.port.out.securityUser.SecurityUserPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUserPersistenceAdapter implements SecurityUserPersistencePort {

    private final SecurityUserRepository repository;

    @Override
    public SecurityUserEntity save(SecurityUserEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<SecurityUserEntity> findByUsername(String username) {
        return repository.findByUsernameIgnoreCase(username);
    }

    @Override
    public Page<SecurityUserEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<SecurityUserEntity> findByActive(Boolean active, Pageable pageable) {
        return repository.findByActive(active, pageable);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }
}