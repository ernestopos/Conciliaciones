package com.conciliaciones.reconciliation.core.application.port.in.securityRole;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole.CreateSecurityRoleRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole.SecurityRoleResponse;

public interface CreateSecurityRoleUseCase {
    SecurityRoleResponse create(CreateSecurityRoleRequest request, String username);
}
