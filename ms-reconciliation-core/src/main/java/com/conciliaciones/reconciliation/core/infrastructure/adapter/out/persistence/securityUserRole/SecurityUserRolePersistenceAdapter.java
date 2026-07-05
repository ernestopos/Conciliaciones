package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.securityUserRole;

import com.conciliaciones.domain.entity.SecurityRoleEntity;
import com.conciliaciones.domain.entity.SecurityUserEntity;
import com.conciliaciones.domain.entity.SecurityUserRoleEntity;
import com.conciliaciones.persistence.repository.SecurityRoleRepository;
import com.conciliaciones.persistence.repository.SecurityUserRepository;
import com.conciliaciones.persistence.repository.SecurityUserRoleRepository;
import com.conciliaciones.reconciliation.core.application.port.out.securityUserRole.SecurityUserRolePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUserRolePersistenceAdapter implements SecurityUserRolePersistencePort {

    private final SecurityUserRepository userRepository;
    private final SecurityRoleRepository roleRepository;
    private final SecurityUserRoleRepository userRoleRepository;

    @Override
    public Optional<SecurityUserEntity> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<SecurityRoleEntity> findRoleById(Long roleId) {
        return roleRepository.findById(roleId);
    }

    @Override
    public Optional<SecurityUserRoleEntity> findByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId);
    }

    @Override
    public List<SecurityUserRoleEntity> findAll() {
        return userRoleRepository.findAll();
    }

    @Override
    public List<SecurityUserRoleEntity> findByActive(Boolean active) {
        return userRoleRepository.findByActive(active);
    }

    @Override
    public SecurityUserRoleEntity save(SecurityUserRoleEntity entity) {
        return userRoleRepository.save(entity);
    }
}