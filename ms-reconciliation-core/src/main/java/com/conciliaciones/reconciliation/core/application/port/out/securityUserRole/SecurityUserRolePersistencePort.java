package com.conciliaciones.reconciliation.core.application.port.out.securityUserRole;

import com.conciliaciones.domain.entity.SecurityRoleEntity;
import com.conciliaciones.domain.entity.SecurityUserEntity;
import com.conciliaciones.domain.entity.SecurityUserRoleEntity;

import java.util.List;
import java.util.Optional;

public interface SecurityUserRolePersistencePort {

    Optional<SecurityUserEntity> findUserById(Long userId);

    Optional<SecurityRoleEntity> findRoleById(Long roleId);

    Optional<SecurityUserRoleEntity> findByUserId(Long userId);

    List<SecurityUserRoleEntity> findAll();

    List<SecurityUserRoleEntity> findByActive(Boolean active);

    SecurityUserRoleEntity save(SecurityUserRoleEntity entity);
}