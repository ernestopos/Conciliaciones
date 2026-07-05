package com.conciliaciones.reconciliation.core.application.port.out.securityMenu;

import com.conciliaciones.domain.entity.SecurityMenuEntity;
import com.conciliaciones.domain.entity.SecuritySubMenuEntity;
import com.conciliaciones.domain.entity.SecurityRoleEntity;
import com.conciliaciones.domain.entity.SecurityRoleMenuPermissionEntity;
import com.conciliaciones.domain.entity.SecurityUserRoleEntity;
import java.util.Optional;
import java.util.List;

public interface SecurityMenuPersistencePort {
    List<SecurityMenuEntity> findMenus(Boolean active);
    Optional<SecurityMenuEntity> findMenuById(Long id);
    List<SecuritySubMenuEntity> findSubMenusByMenuId(Long menuId, Boolean active);
    List<SecuritySubMenuEntity> findSubMenusByMenuIds(List<Long> menuIds, Boolean active);
    Optional<SecurityRoleEntity> findRoleByCode(String roleCode);
    Optional<SecurityUserRoleEntity> findActiveUserRoleByUsername(String username);
    List<SecurityRoleMenuPermissionEntity> findActivePermissionsByRoleCode(String roleCode);
    List<SecurityRoleMenuPermissionEntity> findActivePermissionsByRoleId(Long roleId);
}
