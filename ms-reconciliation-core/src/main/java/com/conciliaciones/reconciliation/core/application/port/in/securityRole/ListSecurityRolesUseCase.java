package com.conciliaciones.reconciliation.core.application.port.in.securityRole;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRole.SecurityRoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListSecurityRolesUseCase {
    Page<SecurityRoleResponse> list(Boolean active, Pageable pageable);
}
