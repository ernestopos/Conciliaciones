package com.conciliaciones.security.application.service;

import com.conciliaciones.security.api.request.AssignPermissionsRequest;
import com.conciliaciones.security.api.request.CreateRoleRequest;
import com.conciliaciones.security.api.request.UpdateRoleRequest;
import com.conciliaciones.security.api.response.RoleResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    public List<RoleResponse> findAll() {
        return List.of(
                new RoleResponse("ADMIN", "Administrador del sistema", List.of("USER_READ", "USER_WRITE", "ROLE_ASSIGN")),
                new RoleResponse("ANALYST", "Analista funcional", List.of("RECONCILIATION_READ"))
        );
    }

    public RoleResponse create(CreateRoleRequest request) {
        return new RoleResponse(request.roleName(), request.description(), List.of());
    }

    public RoleResponse findByName(String roleName) {
        return new RoleResponse(roleName, "Rol de ejemplo", List.of("USER_READ"));
    }

    public RoleResponse update(String roleName, UpdateRoleRequest request) {
        return new RoleResponse(roleName, request.description(), List.of("USER_READ"));
    }

    public RoleResponse assignPermissions(String roleName, AssignPermissionsRequest request) {
        return new RoleResponse(roleName, "Rol actualizado", request.permissions());
    }
}
