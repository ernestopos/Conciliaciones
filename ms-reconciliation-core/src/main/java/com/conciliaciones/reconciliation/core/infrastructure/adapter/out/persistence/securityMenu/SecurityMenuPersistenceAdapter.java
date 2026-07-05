package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.securityMenu;

import com.conciliaciones.domain.entity.*;
import com.conciliaciones.persistence.repository.*;
import com.conciliaciones.reconciliation.core.application.port.out.securityMenu.SecurityMenuPersistencePort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityMenuPersistenceAdapter implements SecurityMenuPersistencePort {

    private final SecurityMenuRepository menuRepository;
    private final SecuritySubMenuRepository subMenuRepository;
    private final SecurityRoleRepository roleRepository;
    private final SecurityUserRoleRepository userRoleRepository;
    private final SecurityRoleMenuPermissionRepository roleMenuPermissionRepository;

    @Override
    public List<SecurityMenuEntity> findMenus(Boolean active) {
        log.info("LOG INICIO X = findSecurityMenusPersistence active={}", active);
        List<SecurityMenuEntity> result = active == null
                ? menuRepository.findAllByOrderBySortOrderAsc()
                : menuRepository.findByActiveOrderBySortOrderAsc(active);
        log.info("LOG FIN X = findSecurityMenusPersistence size={}", result.size());
        return result;
    }

    @Override
    public Optional<SecurityMenuEntity> findMenuById(Long id) {
        log.info("LOG INICIO X = findSecurityMenuByIdPersistence id={}", id);
        Optional<SecurityMenuEntity> result = menuRepository.findById(id);
        log.info("LOG FIN X = findSecurityMenuByIdPersistence found={}", result.isPresent());
        return result;
    }

    @Override
    public List<SecuritySubMenuEntity> findSubMenusByMenuId(Long menuId, Boolean active) {
        log.info("LOG INICIO X = findSecuritySubMenusByMenuIdPersistence menuId={} active={}", menuId, active);
        List<SecuritySubMenuEntity> result = active == null
                ? subMenuRepository.findByMenuIdOrderBySortOrderAsc(menuId)
                : subMenuRepository.findByMenuIdAndActiveOrderBySortOrderAsc(menuId, active);
        log.info("LOG FIN X = findSecuritySubMenusByMenuIdPersistence size={}", result.size());
        return result;
    }

    @Override
    public List<SecuritySubMenuEntity> findSubMenusByMenuIds(List<Long> menuIds, Boolean active) {
        log.info("LOG INICIO X = findSecuritySubMenusByMenuIdsPersistence menuIds={} active={}", menuIds.size(), active);
        List<SecuritySubMenuEntity> result = active == null
                ? subMenuRepository.findByMenuIdInOrderByMenuSortOrderAscSortOrderAsc(menuIds)
                : subMenuRepository.findByMenuIdInAndActiveOrderByMenuSortOrderAscSortOrderAsc(menuIds, active);
        log.info("LOG FIN X = findSecuritySubMenusByMenuIdsPersistence size={}", result.size());
        return result;
    }

    @Override
    public Optional<SecurityRoleEntity> findRoleByCode(String roleCode) {
        return roleRepository.findByCodeIgnoreCaseAndActiveTrue(roleCode);
    }

    @Override
    public Optional<SecurityUserRoleEntity> findActiveUserRoleByUsername(String username) {
        return userRoleRepository.findByUserUsernameIgnoreCaseAndActiveTrue(username);
    }

    @Override
    public List<SecurityRoleMenuPermissionEntity> findActivePermissionsByRoleCode(String roleCode) {
        return roleMenuPermissionRepository.findActivePermissionsByRoleCode(roleCode);
    }

    @Override
    public List<SecurityRoleMenuPermissionEntity> findActivePermissionsByRoleId(Long roleId) {
        return roleMenuPermissionRepository.findActivePermissionsByRoleId(roleId);
    }
}
