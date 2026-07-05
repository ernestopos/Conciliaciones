package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.securityRoleMenuPermission;

import com.conciliaciones.domain.entity.SecurityRoleEntity;
import com.conciliaciones.domain.entity.SecurityRoleMenuPermissionEntity;
import com.conciliaciones.domain.entity.SecuritySubMenuEntity;
import com.conciliaciones.persistence.repository.SecurityRoleMenuPermissionRepository;
import com.conciliaciones.persistence.repository.SecurityRoleRepository;
import com.conciliaciones.persistence.repository.SecuritySubMenuRepository;
import com.conciliaciones.reconciliation.core.application.port.out.securityRoleMenuPermission.SecurityRoleMenuPermissionPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityRoleMenuPermissionPersistenceAdapter implements SecurityRoleMenuPermissionPersistencePort {

    private final SecurityRoleRepository roleRepository;
    private final SecuritySubMenuRepository subMenuRepository;
    private final SecurityRoleMenuPermissionRepository permissionRepository;

    @Override
    public Optional<SecurityRoleEntity> findRoleById(Long roleId) {
        return roleRepository.findById(roleId);
    }

    @Override
    public List<SecuritySubMenuEntity> findSubMenusByIds(List<Long> subMenuIds) {
        return subMenuRepository.findActiveByIdInWithMenu(subMenuIds);
    }

    @Override
    public List<SecurityRoleMenuPermissionEntity> findByRoleId(Long roleId) {
        return permissionRepository.findByRoleId(roleId);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        permissionRepository.deleteByRoleId(roleId);
        permissionRepository.flush();
    }

    @Override
    public List<SecurityRoleMenuPermissionEntity> saveAll(List<SecurityRoleMenuPermissionEntity> entities) {
        return permissionRepository.saveAll(entities);
    }

    @Override
    public List<SecurityRoleMenuPermissionEntity> findActivePermissionsByRoleId(Long roleId) {
        return permissionRepository.findActivePermissionsByRoleId(roleId);
    }
}
