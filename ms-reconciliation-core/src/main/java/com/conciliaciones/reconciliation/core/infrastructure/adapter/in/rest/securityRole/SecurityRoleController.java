package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.securityRole;

import com.conciliaciones.reconciliation.core.application.port.in.securityRole.CreateSecurityRoleUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityRole.DeleteSecurityRoleUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityRole.GetSecurityRoleByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityRole.ListSecurityRolesUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityRole.UpdateSecurityRoleUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole.CreateSecurityRoleRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole.SecurityRoleResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole.UpdateSecurityRoleRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.support.AuthenticatedUserResolver;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core/v1/security/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Security Roles", description = "Operaciones CRUD para roles de seguridad")
public class SecurityRoleController {

    private final CreateSecurityRoleUseCase createSecurityRoleUseCase;
    private final GetSecurityRoleByIdUseCase getSecurityRoleByIdUseCase;
    private final ListSecurityRolesUseCase listSecurityRolesUseCase;
    private final UpdateSecurityRoleUseCase updateSecurityRoleUseCase;
    private final DeleteSecurityRoleUseCase deleteSecurityRoleUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SecurityRoleResponse create(@Valid @RequestBody CreateSecurityRoleRequest request, @AuthenticationPrincipal Jwt jwt) {
        log.info("LOG INICIO X = createSecurityRoleController");
        SecurityRoleResponse response = createSecurityRoleUseCase.create(request, AuthenticatedUserResolver.resolveUsername(jwt));
        log.info("LOG FIN X = createSecurityRoleController id={}", response.id());
        return response;
    }

    @GetMapping("/{id}")
    public SecurityRoleResponse getById(@PathVariable Long id) {
        log.info("LOG INICIO X = getSecurityRoleByIdController id={}", id);
        SecurityRoleResponse response = getSecurityRoleByIdUseCase.getById(id);
        log.info("LOG FIN X = getSecurityRoleByIdController id={}", response.id());
        return response;
    }

    @GetMapping
    public Page<SecurityRoleResponse> list(@RequestParam(required = false) Boolean active, Pageable pageable) {
        log.info("LOG INICIO X = listSecurityRolesController active={} page={} size={}", active, pageable.getPageNumber(), pageable.getPageSize());
        Page<SecurityRoleResponse> response = listSecurityRolesUseCase.list(active, pageable);
        log.info("LOG FIN X = listSecurityRolesController totalElements={}", response.getTotalElements());
        return response;
    }

    @PutMapping("/{id}")
    public SecurityRoleResponse update(@PathVariable Long id, @Valid @RequestBody UpdateSecurityRoleRequest request, @AuthenticationPrincipal Jwt jwt) {
        log.info("LOG INICIO X = updateSecurityRoleController id={}", id);
        SecurityRoleResponse response = updateSecurityRoleUseCase.update(id, request, AuthenticatedUserResolver.resolveUsername(jwt));
        log.info("LOG FIN X = updateSecurityRoleController id={}", response.id());
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        log.info("LOG INICIO X = deleteSecurityRoleController id={}", id);
        deleteSecurityRoleUseCase.delete(id, AuthenticatedUserResolver.resolveUsername(jwt));
        log.info("LOG FIN X = deleteSecurityRoleController id={}", id);
    }
}
