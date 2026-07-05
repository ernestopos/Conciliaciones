package com.conciliaciones.reconciliation.core.application.port.in.securityRole;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole.SecurityRoleResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole.UpdateSecurityRoleRequest;

public interface UpdateSecurityRoleUseCase {
    SecurityRoleResponse update(Long id, UpdateSecurityRoleRequest request, String username);
}
