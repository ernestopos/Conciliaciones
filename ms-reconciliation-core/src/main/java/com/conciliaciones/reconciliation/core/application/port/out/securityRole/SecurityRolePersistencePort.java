package com.conciliaciones.reconciliation.core.application.port.out.securityRole;

import com.conciliaciones.domain.entity.SecurityRoleEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SecurityRolePersistencePort {
    SecurityRoleEntity save(SecurityRoleEntity entity);
    Optional<SecurityRoleEntity> findById(Long id);
    Page<SecurityRoleEntity> findAll(Pageable pageable);
    Page<SecurityRoleEntity> findByActive(Boolean active, Pageable pageable);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
}
