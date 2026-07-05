package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRoleMenuPermission;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SaveSecurityRoleMenuPermissionRequest(
        @NotEmpty
        List<Long> subMenuIds
) {
}