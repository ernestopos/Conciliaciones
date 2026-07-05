package com.conciliaciones.reconciliation.core.application.port.in.securityUserRole;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUserRole.SaveSecurityUserRoleRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUserRole.SecurityUserRoleResponse;

public interface SaveSecurityUserRoleUseCase {
    SecurityUserRoleResponse save(SaveSecurityUserRoleRequest request, String username);
}