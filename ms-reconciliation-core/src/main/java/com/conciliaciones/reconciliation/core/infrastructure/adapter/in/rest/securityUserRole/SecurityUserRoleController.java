package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.securityUserRole;

import com.conciliaciones.reconciliation.core.application.port.in.securityUserRole.DeleteSecurityUserRoleUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityUserRole.ListSecurityUserRoleUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityUserRole.SaveSecurityUserRoleUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUserRole.SaveSecurityUserRoleRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUserRole.SecurityUserRoleResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.support.AuthenticatedUserResolver;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/security/user-roles")
@RequiredArgsConstructor
public class SecurityUserRoleController {

    private final SaveSecurityUserRoleUseCase saveUseCase;
    private final DeleteSecurityUserRoleUseCase deleteUseCase;
    private final ListSecurityUserRoleUseCase listUseCase;

    @GetMapping
    public List<SecurityUserRoleResponse> list(@RequestParam(required = false) Boolean active) {
        return listUseCase.list(active);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SecurityUserRoleResponse save(@Valid @RequestBody SaveSecurityUserRoleRequest request,
                                         @AuthenticationPrincipal Jwt jwt) {
        return saveUseCase.save(request, AuthenticatedUserResolver.resolveUsername(jwt));
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByUserId(@PathVariable Long userId,
                               @AuthenticationPrincipal Jwt jwt) {
        deleteUseCase.deleteByUserId(userId, AuthenticatedUserResolver.resolveUsername(jwt));
    }
}