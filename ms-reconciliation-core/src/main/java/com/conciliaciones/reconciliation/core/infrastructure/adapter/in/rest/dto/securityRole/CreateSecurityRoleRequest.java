package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSecurityRoleRequest(
        @NotBlank @Size(max = 100) String code,
        @NotBlank @Size(max = 150) String name,
        @Size(max = 500) String description,
        Boolean active
) {
}
