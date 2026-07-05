package com.conciliaciones.reconciliation.core.application.usecase.securityMenu;

import com.conciliaciones.domain.entity.*;
import com.conciliaciones.reconciliation.core.application.port.in.securityMenu.*;
import com.conciliaciones.reconciliation.core.application.port.out.securityMenu.SecurityMenuPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu.SecurityMenuResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu.SecurityMenuTreeResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityMenu.SecuritySubMenuResponse;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityMenuService implements ListSecurityMenusUseCase, ListSecuritySubMenusUseCase, ListSecurityMenuTreeUseCase, ListAdminSecurityMenuTreeUseCase,
        ListUserSecurityMenuTreeUseCase {

    private final SecurityMenuPersistencePort persistencePort;

    @Override
    @Transactional(readOnly = true)
    public List<SecurityMenuResponse> list(Boolean active) {
        log.info("LOG INICIO X = listSecurityMenus active={}", active);
        List<SecurityMenuResponse> result = persistencePort.findMenus(active)
                .stream()
                .map(this::toMenuResponse)
                .toList();
        log.info("LOG FIN X = listSecurityMenus size={}", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SecurityMenuTreeResponse> listTree(Boolean active) {
        log.info("LOG INICIO X = listSecurityMenuTree active={}", active);

        List<SecurityMenuEntity> menus = persistencePort.findMenus(active);
        if (menus.isEmpty()) {
            log.info("LOG FIN X = listSecurityMenuTree size=0");
            return Collections.emptyList();
        }

        List<Long> menuIds = menus.stream()
                .map(SecurityMenuEntity::getId)
                .toList();

        Map<Long, List<SecuritySubMenuResponse>> subMenusByMenuId = persistencePort.findSubMenusByMenuIds(menuIds, active)
                .stream()
                .map(this::toSubMenuResponse)
                .collect(Collectors.groupingBy(SecuritySubMenuResponse::menuId));

        List<SecurityMenuTreeResponse> result = menus.stream()
                .map(menu -> toMenuTreeResponse(menu, subMenusByMenuId.getOrDefault(menu.getId(), Collections.emptyList())))
                .toList();

        log.info("LOG FIN X = listSecurityMenuTree size={}", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SecuritySubMenuResponse> listByMenuId(Long menuId, Boolean active) {
        log.info("LOG INICIO X = listSecuritySubMenus menuId={} active={}", menuId, active);
        persistencePort.findMenuById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menú no encontrado con id: " + menuId));

        List<SecuritySubMenuResponse> result = persistencePort.findSubMenusByMenuId(menuId, active)
                .stream()
                .map(this::toSubMenuResponse)
                .toList();
        log.info("LOG FIN X = listSecuritySubMenus size={}", result.size());
        return result;
    }

    private SecurityMenuResponse toMenuResponse(SecurityMenuEntity entity) {
        ParameterEntity parameter = entity.getParameter();
        return new SecurityMenuResponse(
                entity.getId(),
                parameter == null ? null : parameter.getId(),
                parameter == null ? null : parameter.getValue(),
                entity.getCode(),
                entity.getLabel(),
                entity.getIcon(),
                entity.getSortOrder(),
                entity.getActive()
        );
    }

    private SecurityMenuTreeResponse toMenuTreeResponse(SecurityMenuEntity entity, List<SecuritySubMenuResponse> children) {
        ParameterEntity parameter = entity.getParameter();
        return new SecurityMenuTreeResponse(
                entity.getId(),
                parameter == null ? null : parameter.getId(),
                parameter == null ? null : parameter.getValue(),
                entity.getCode(),
                entity.getLabel(),
                entity.getIcon(),
                entity.getSortOrder(),
                entity.getActive(),
                children
        );
    }

    private SecuritySubMenuResponse toSubMenuResponse(SecuritySubMenuEntity entity) {
        ParameterEntity parameter = entity.getParameter();
        SecurityMenuEntity menu = entity.getMenu();
        return new SecuritySubMenuResponse(
                entity.getId(),
                menu == null ? null : menu.getId(),
                menu == null ? null : menu.getCode(),
                parameter == null ? null : parameter.getId(),
                parameter == null ? null : parameter.getValue(),
                entity.getCode(),
                entity.getLabel(),
                entity.getRoute(),
                entity.getIcon(),
                entity.getSortOrder(),
                entity.getActive()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SecurityMenuTreeResponse> listAdminMenuTree() {
        persistencePort.findRoleByCode("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Rol ADMIN no encontrado"));

        return buildTreeFromPermissions(
                persistencePort.findActivePermissionsByRoleCode("ADMIN")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SecurityMenuTreeResponse> listUserMenuTree(String username) {
        Optional<SecurityUserRoleEntity> userRole =
                persistencePort.findActiveUserRoleByUsername(username);

        if (userRole.isEmpty()) {
            return List.of();
        }

        return buildTreeFromPermissions(
                persistencePort.findActivePermissionsByRoleId(userRole.get().getRole().getId())
        );
    }

    private List<SecurityMenuTreeResponse> buildTreeFromPermissions(List<SecurityRoleMenuPermissionEntity> permissions) {
        Map<Long, List<SecurityRoleMenuPermissionEntity>> permissionsByMenu = permissions.stream()
                .collect(Collectors.groupingBy(permission -> permission.getMenu().getId()));

        return permissionsByMenu.values()
                .stream()
                .map(menuPermissions -> {
                    SecurityMenuEntity menu = menuPermissions.get(0).getMenu();

                    List<SecuritySubMenuResponse> children = menuPermissions.stream()
                            .filter(permission -> permission.getSubMenu() != null)
                            .map(permission -> toSubMenuResponse(permission.getSubMenu()))
                            .toList();

                    return toMenuTreeResponse(menu, children);
                })
                .sorted((a, b) -> a.sortOrder().compareTo(b.sortOrder()))
                .toList();
    }

}
