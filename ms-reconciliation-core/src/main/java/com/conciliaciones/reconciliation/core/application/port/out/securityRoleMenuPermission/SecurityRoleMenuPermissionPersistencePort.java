package com.conciliaciones.reconciliation.core.application.port.out.securityRoleMenuPermission;

import com.conciliaciones.domain.entity.SecurityRoleEntity;
import com.conciliaciones.domain.entity.SecurityRoleMenuPermissionEntity;
import com.conciliaciones.domain.entity.SecuritySubMenuEntity;
import java.util.List;
import java.util.Optional;

public interface SecurityRoleMenuPermissionPersistencePort {

    Optional<SecurityRoleEntity> findRoleById(Long roleId);

    List<SecuritySubMenuEntity> findSubMenusByIds(List<Long> subMenuIds);

    List<SecurityRoleMenuPermissionEntity> findByRoleId(Long roleId);

    void deleteByRoleId(Long roleId);

    List<SecurityRoleMenuPermissionEntity> saveAll(List<SecurityRoleMenuPermissionEntity> entities);

    List<SecurityRoleMenuPermissionEntity> findActivePermissionsByRoleId(Long roleId);
}