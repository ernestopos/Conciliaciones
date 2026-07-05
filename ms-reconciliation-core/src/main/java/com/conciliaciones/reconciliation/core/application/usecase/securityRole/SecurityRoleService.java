package com.conciliaciones.reconciliation.core.application.usecase.securityRole;

import com.conciliaciones.domain.entity.SecurityRoleEntity;
import com.conciliaciones.reconciliation.core.application.port.in.securityRole.CreateSecurityRoleUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityRole.DeleteSecurityRoleUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityRole.GetSecurityRoleByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityRole.ListSecurityRolesUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityRole.UpdateSecurityRoleUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.securityRole.SecurityRolePersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole.CreateSecurityRoleRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole.SecurityRoleResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole.UpdateSecurityRoleRequest;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityRoleService implements CreateSecurityRoleUseCase, GetSecurityRoleByIdUseCase, ListSecurityRolesUseCase, UpdateSecurityRoleUseCase, DeleteSecurityRoleUseCase {

    private final SecurityRolePersistencePort persistencePort;

    @Override
    public SecurityRoleResponse create(CreateSecurityRoleRequest request, String username) {
        log.info("LOG INICIO X = createSecurityRole code={}", request.code());

        String code = normalizeCode(request.code());
        validateUniqueCode(code);

        SecurityRoleEntity entity = SecurityRoleEntity.builder()
                .code(code)
                .name(request.name().trim())
                .description(normalizeText(request.description()))
                .active(request.active() == null ? Boolean.TRUE : request.active())
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();

        SecurityRoleEntity saved = persistencePort.save(entity);
        log.info("LOG FIN X = createSecurityRole id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public SecurityRoleResponse getById(Long id) {
        log.info("LOG INICIO X = getSecurityRoleById id={}", id);
        SecurityRoleEntity entity = findExisting(id);
        log.info("LOG FIN X = getSecurityRoleById id={}", entity.getId());
        return toResponse(entity);
    }

    @Override
    public Page<SecurityRoleResponse> list(Boolean active, Pageable pageable) {
        log.info("LOG INICIO X = listSecurityRoles active={} page={} size={}", active, pageable.getPageNumber(), pageable.getPageSize());
        Page<SecurityRoleResponse> result = (active == null ? persistencePort.findAll(pageable) : persistencePort.findByActive(active, pageable))
                .map(this::toResponse);
        log.info("LOG FIN X = listSecurityRoles totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public SecurityRoleResponse update(Long id, UpdateSecurityRoleRequest request, String username) {
        log.info("LOG INICIO X = updateSecurityRole id={}", id);

        SecurityRoleEntity entity = findExisting(id);
        String code = normalizeCode(request.code());
        validateUniqueCodeForUpdate(code, id);

        entity.setCode(code);
        entity.setName(request.name().trim());
        entity.setDescription(normalizeText(request.description()));
        entity.setActive(request.active() == null ? Boolean.TRUE : request.active());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(username);

        SecurityRoleEntity saved = persistencePort.save(entity);
        log.info("LOG FIN X = updateSecurityRole id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public void delete(Long id, String username) {
        log.info("LOG INICIO X = deleteSecurityRole id={}", id);
        SecurityRoleEntity entity = findExisting(id);
        entity.setActive(Boolean.FALSE);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(username);
        persistencePort.save(entity);
        log.info("LOG FIN X = deleteSecurityRole id={}", id);
    }

    private SecurityRoleEntity findExisting(Long id) {
        return persistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
    }

    private void validateUniqueCode(String code) {
        if (persistencePort.existsByCode(code)) {
            throw new IllegalArgumentException("Ya existe un rol con el código: " + code);
        }
    }

    private void validateUniqueCodeForUpdate(String code, Long id) {
        if (persistencePort.existsByCodeAndIdNot(code, id)) {
            throw new IllegalArgumentException("Ya existe un rol con el código: " + code);
        }
    }

    private String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase();
    }

    private String normalizeText(String text) {
        return text == null || text.trim().isEmpty() ? null : text.trim();
    }

    private SecurityRoleResponse toResponse(SecurityRoleEntity entity) {
        return new SecurityRoleResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }
}
