package com.conciliaciones.security.api.controller;

import com.conciliaciones.security.api.request.AssignRolesRequest;
import com.conciliaciones.security.api.request.CreateUserRequest;
import com.conciliaciones.security.api.request.ResetPasswordRequest;
import com.conciliaciones.security.api.request.UpdateUserRequest;
import com.conciliaciones.security.api.request.UpdateUserStatusRequest;
import com.conciliaciones.security.api.response.UserResponse;
import com.conciliaciones.security.application.service.UserService;
import com.conciliaciones.security.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Administración de usuarios")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_admin') or hasRole('ADMIN')")
    @Operation(summary = "Crear usuario")
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario creado", userService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios")
    public ResponseEntity<ApiResponse<List<UserResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Usuarios consultados", userService.findAll()));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Consultar usuario por id")
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario consultado", userService.findById(userId)));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable String userId,
                                                            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado", userService.update(userId, request)));
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "Cambiar estado del usuario")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(@PathVariable String userId,
                                                                  @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado", userService.updateStatus(userId, request)));
    }

    @PatchMapping("/{userId}/reset-password")
    @Operation(summary = "Reiniciar contraseña")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable String userId,
                                                           @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Contraseña reiniciada", null));
    }

    @PostMapping("/{userId}/roles")
    @Operation(summary = "Asignar roles a usuario")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoles(@PathVariable String userId,
                                                                 @Valid @RequestBody AssignRolesRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Roles asignados", userService.assignRoles(userId, request)));
    }
}
