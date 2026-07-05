package com.conciliaciones.reconciliation.core.application.usecase.securityRoleMenuPermission;

import com.conciliaciones.domain.entity.SecurityRoleEntity;
import com.conciliaciones.domain.entity.SecurityRoleMenuPermissionEntity;
import com.conciliaciones.domain.entity.SecuritySubMenuEntity;
import com.conciliaciones.reconciliation.core.application.port.in.securityRoleMenuPermission.ListSecurityRoleMenuPermissionUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityRoleMenuPermission.SaveSecurityRoleMenuPermissionUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.securityRoleMenuPermission.SecurityRoleMenuPermissionPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRoleMenuPermission.SaveSecurityRoleMenuPermissionRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRoleMenuPermission.SecurityRoleMenuPermissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SecurityRoleMenuPermissionService implements SaveSecurityRoleMenuPermissionUseCase, ListSecurityRoleMenuPermissionUseCase {

    private final SecurityRoleMenuPermissionPersistencePort persistencePort;

    @Override
    public List<SecurityRoleMenuPermissionResponse> listByRoleId(Long roleId) {
        return persistencePort.findByRoleId(roleId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<SecurityRoleMenuPermissionResponse> save(Long roleId,SaveSecurityRoleMenuPermissionRequest request,String username) {
        SecurityRoleEntity role = persistencePort.findRoleById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        List<SecuritySubMenuEntity> subMenus = persistencePort.findSubMenusByIds(request.subMenuIds());

        persistencePort.deleteByRoleId(roleId);
        List<SecurityRoleMenuPermissionEntity> permissions = subMenus.stream()
                .map(subMenu -> SecurityRoleMenuPermissionEntity.builder()
                        .role(role)
                        .menu(subMenu.getMenu())
                        .subMenu(subMenu)
                        .active(Boolean.TRUE)
                        .createdAt(LocalDateTime.now())
                        .createdBy(username)
                        .build())
                .toList();
        persistencePort.saveAll(permissions);
        return persistencePort.findActivePermissionsByRoleId(roleId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private SecurityRoleMenuPermissionResponse toResponse(SecurityRoleMenuPermissionEntity entity) {
        return new SecurityRoleMenuPermissionResponse(
                entity.getId(),
                entity.getRole().getId(),
                entity.getRole().getCode(),
                entity.getRole().getName(),
                entity.getMenu().getId(),
                entity.getMenu().getCode(),
                entity.getSubMenu().getId(),
                entity.getSubMenu().getCode(),
                entity.getSubMenu().getLabel(),
                entity.getSubMenu().getRoute(),
                entity.getActive()
        );
    }
}
