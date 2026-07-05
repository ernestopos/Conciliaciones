package com.conciliaciones.reconciliation.core.application.usecase.securityUser;

import com.conciliaciones.domain.entity.SecurityUserEntity;
import com.conciliaciones.reconciliation.core.application.port.in.securityUser.ConfigureSecurityUserUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityUser.ListConfiguredSecurityUsersUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.securityUser.SecurityUserPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUser.ConfigureSecurityUserRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUser.SecurityUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityUserService implements ConfigureSecurityUserUseCase, ListConfiguredSecurityUsersUseCase {

    private final SecurityUserPersistencePort persistencePort;

    @Override
    public SecurityUserResponse configure(ConfigureSecurityUserRequest request, String username) {
        SecurityUserEntity entity = persistencePort.findByUsername(request.username())
                .orElseGet(() -> SecurityUserEntity.builder()
                        .username(request.username().trim())
                        .email(request.email().trim())
                        .fullName(request.fullName().trim())
                        .active(request.active() == null ? Boolean.TRUE : request.active())
                        .createdAt(LocalDateTime.now())
                        .createdBy(username)
                        .build());

        entity.setEmail(request.email().trim());
        entity.setFullName(request.fullName().trim());
        entity.setActive(request.active() == null ? Boolean.TRUE : request.active());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(username);

        return toResponse(persistencePort.save(entity));
    }

    @Override
    public Page<SecurityUserResponse> list(Boolean active, Pageable pageable) {
        return (active == null ? persistencePort.findAll(pageable) : persistencePort.findByActive(active, pageable))
                .map(this::toResponse);
    }

    private SecurityUserResponse toResponse(SecurityUserEntity entity) {
        return new SecurityUserResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getFullName(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }
}