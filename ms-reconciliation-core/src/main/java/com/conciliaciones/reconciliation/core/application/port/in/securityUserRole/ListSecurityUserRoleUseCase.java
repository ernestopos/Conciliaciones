package com.conciliaciones.reconciliation.core.application.port.in.securityUserRole;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUserRole.SecurityUserRoleResponse;

import java.util.List;

public interface ListSecurityUserRoleUseCase {
    List<SecurityUserRoleResponse> list(Boolean active);
}