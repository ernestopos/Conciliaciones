package com.conciliaciones.reconciliation.core.application.usecase.securityUserRole;

import com.conciliaciones.domain.entity.SecurityRoleEntity;
import com.conciliaciones.domain.entity.SecurityUserEntity;
import com.conciliaciones.domain.entity.SecurityUserRoleEntity;
import com.conciliaciones.reconciliation.core.application.port.in.securityUserRole.DeleteSecurityUserRoleUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityUserRole.ListSecurityUserRoleUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityUserRole.SaveSecurityUserRoleUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.securityUserRole.SecurityUserRolePersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUserRole.SaveSecurityUserRoleRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUserRole.SecurityUserRoleResponse;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SecurityUserRoleService
        implements SaveSecurityUserRoleUseCase, DeleteSecurityUserRoleUseCase, ListSecurityUserRoleUseCase {

    private final SecurityUserRolePersistencePort persistencePort;

    @Override
    public SecurityUserRoleResponse save(SaveSecurityUserRoleRequest request, String username) {
        SecurityUserEntity user = persistencePort.findUserById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.userId()));

        SecurityRoleEntity role = persistencePort.findRoleById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + request.roleId()));

        SecurityUserRoleEntity entity = persistencePort.findByUserId(request.userId())
                .orElseGet(() -> SecurityUserRoleEntity.builder()
                        .user(user)
                        .createdAt(LocalDateTime.now())
                        .createdBy(username)
                        .build());

        entity.setRole(role);
        entity.setActive(Boolean.TRUE);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(username);

        return toResponse(persistencePort.save(entity));
    }

    @Override
    public void deleteByUserId(Long userId, String username) {
        SecurityUserRoleEntity entity = persistencePort.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Asociación usuario-rol no encontrada para userId: " + userId));

        entity.setActive(Boolean.FALSE);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(username);

        persistencePort.save(entity);
    }

    @Override
    public List<SecurityUserRoleResponse> list(Boolean active) {
        List<SecurityUserRoleEntity> records = active == null
                ? persistencePort.findAll()
                : persistencePort.findByActive(active);

        return records.stream()
                .map(this::toResponse)
                .toList();
    }

    private SecurityUserRoleResponse toResponse(SecurityUserRoleEntity entity) {
        return new SecurityUserRoleResponse(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUser().getUsername(),
                entity.getUser().getEmail(),
                entity.getUser().getFullName(),
                entity.getRole().getId(),
                entity.getRole().getCode(),
                entity.getRole().getName(),
                entity.getActive()
        );
    }
}