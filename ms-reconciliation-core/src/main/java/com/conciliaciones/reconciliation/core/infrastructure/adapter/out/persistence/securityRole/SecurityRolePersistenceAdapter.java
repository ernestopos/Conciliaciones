package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.securityRole;

import com.conciliaciones.domain.entity.SecurityRoleEntity;
import com.conciliaciones.persistence.repository.SecurityRoleRepository;
import com.conciliaciones.reconciliation.core.application.port.out.securityRole.SecurityRolePersistencePort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityRolePersistenceAdapter implements SecurityRolePersistencePort {

    private final SecurityRoleRepository repository;

    @Override
    public SecurityRoleEntity save(SecurityRoleEntity entity) {
        log.info("LOG INICIO X = saveSecurityRolePersistence");
        SecurityRoleEntity saved = repository.save(entity);
        log.info("LOG FIN X = saveSecurityRolePersistence id={}", saved.getId());
        return saved;
    }

    @Override
    public Optional<SecurityRoleEntity> findById(Long id) {
        log.info("LOG INICIO X = findSecurityRoleByIdPersistence id={}", id);
        Optional<SecurityRoleEntity> result = repository.findById(id);
        log.info("LOG FIN X = findSecurityRoleByIdPersistence found={}", result.isPresent());
        return result;
    }

    @Override
    public Page<SecurityRoleEntity> findAll(Pageable pageable) {
        log.info("LOG INICIO X = findAllSecurityRolePersistence");
        Page<SecurityRoleEntity> result = repository.findAll(pageable);
        log.info("LOG FIN X = findAllSecurityRolePersistence totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public Page<SecurityRoleEntity> findByActive(Boolean active, Pageable pageable) {
        log.info("LOG INICIO X = findSecurityRoleByActivePersistence active={}", active);
        Page<SecurityRoleEntity> result = repository.findByActive(active, pageable);
        log.info("LOG FIN X = findSecurityRoleByActivePersistence totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCodeIgnoreCase(code);
    }

    @Override
    public boolean existsByCodeAndIdNot(String code, Long id) {
        return repository.existsByCodeIgnoreCaseAndIdNot(code, id);
    }
}
