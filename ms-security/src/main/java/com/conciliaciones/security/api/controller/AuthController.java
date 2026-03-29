package com.conciliaciones.security.api.controller;

import com.conciliaciones.security.api.request.LoginRequest;
import com.conciliaciones.security.api.request.LogoutRequest;
import com.conciliaciones.security.api.request.RefreshTokenRequest;
import com.conciliaciones.security.api.response.LoginResponse;
import com.conciliaciones.security.application.service.AuthService;
import com.conciliaciones.security.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Operaciones de autenticación")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Autenticación exitosa", authService.login(request)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Token refrescado", authService.refresh(request)));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.ok("Sesión cerrada", null));
    }
}
