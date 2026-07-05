package com.conciliaciones.reconciliation.core.application.port.in.securityRole;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole.SecurityRoleResponse;

public interface GetSecurityRoleByIdUseCase {
    SecurityRoleResponse getById(Long id);
}
