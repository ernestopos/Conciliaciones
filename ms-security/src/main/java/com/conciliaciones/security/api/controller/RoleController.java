package com.conciliaciones.security.api.controller;

import com.conciliaciones.security.api.request.AssignPermissionsRequest;
import com.conciliaciones.security.api.request.CreateRoleRequest;
import com.conciliaciones.security.api.request.UpdateRoleRequest;
import com.conciliaciones.security.api.response.RoleResponse;
import com.conciliaciones.security.application.service.RoleService;
import com.conciliaciones.security.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Administración de roles y permisos")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Listar roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Roles consultados", roleService.findAll()));
    }

    @PostMapping
    @Operation(summary = "Crear rol")
    public ResponseEntity<ApiResponse<RoleResponse>> create(@Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Rol creado", roleService.create(request)));
    }

    @GetMapping("/{roleName}")
    @Operation(summary = "Consultar rol por nombre")
    public ResponseEntity<ApiResponse<RoleResponse>> findByName(@PathVariable String roleName) {
        return ResponseEntity.ok(ApiResponse.ok("Rol consultado", roleService.findByName(roleName)));
    }

    @PutMapping("/{roleName}")
    @Operation(summary = "Actualizar rol")
    public ResponseEntity<ApiResponse<RoleResponse>> update(@PathVariable String roleName,
                                                            @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Rol actualizado", roleService.update(roleName, request)));
    }

    @PostMapping("/{roleName}/permissions")
    @Operation(summary = "Asignar permisos a rol")
    public ResponseEntity<ApiResponse<RoleResponse>> assignPermissions(@PathVariable String roleName,
                                                                       @Valid @RequestBody AssignPermissionsRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Permisos asignados", roleService.assignPermissions(roleName, request)));
    }
}
