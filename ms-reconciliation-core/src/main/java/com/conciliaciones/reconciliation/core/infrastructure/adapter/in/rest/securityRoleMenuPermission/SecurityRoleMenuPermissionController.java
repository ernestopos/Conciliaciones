package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.securityRoleMenuPermission;

import com.conciliaciones.reconciliation.core.application.port.in.securityRoleMenuPermission.ListSecurityRoleMenuPermissionUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.securityRoleMenuPermission.SaveSecurityRoleMenuPermissionUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRoleMenuPermission.SaveSecurityRoleMenuPermissionRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRoleMenuPermission.SecurityRoleMenuPermissionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/core/v1/security/roles")
@RequiredArgsConstructor
public class SecurityRoleMenuPermissionController {

    private final SaveSecurityRoleMenuPermissionUseCase saveUseCase;
    private final ListSecurityRoleMenuPermissionUseCase listUseCase;

    @GetMapping("/{roleId}/menu-permissions")
    public List<SecurityRoleMenuPermissionResponse> listByRole(@PathVariable Long roleId) {
        return listUseCase.listByRoleId(roleId);
    }

    @PutMapping("/{roleId}/menu-permissions")
    public List<SecurityRoleMenuPermissionResponse> save(
            @PathVariable Long roleId,
            @Valid @RequestBody SaveSecurityRoleMenuPermissionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String username = jwt.getClaimAsString("preferred_username");
        return saveUseCase.save(roleId, request, username);
    }
}
