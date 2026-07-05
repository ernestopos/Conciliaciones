package com.conciliaciones.reconciliation.core.application.port.in.securityRoleMenuPermission;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRoleMenuPermission.SaveSecurityRoleMenuPermissionRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityRoleMenuPermission.SecurityRoleMenuPermissionResponse;
import java.util.List;

public interface SaveSecurityRoleMenuPermissionUseCase {

    List<SecurityRoleMenuPermissionResponse> save(Long roleId, SaveSecurityRoleMenuPermissionRequest request, String username);
}