package com.conciliaciones.security.api.controller;

import com.conciliaciones.security.api.response.AuthenticatedUserResponse;
import com.conciliaciones.security.application.service.ProfileService;
import com.conciliaciones.security.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/security/me")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Perfil del usuario autenticado")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "Consultar usuario autenticado")
    public ResponseEntity<ApiResponse<AuthenticatedUserResponse>> me(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok("Perfil consultado", profileService.getAuthenticatedUser(authentication)));
    }

    @GetMapping("/roles")
    @Operation(summary = "Consultar roles del usuario autenticado")
    public ResponseEntity<ApiResponse<List<String>>> roles(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok("Roles consultados", profileService.getRoles(authentication)));
    }

    @GetMapping("/permissions")
    @Operation(summary = "Consultar permisos del usuario autenticado")
    public ResponseEntity<ApiResponse<List<String>>> permissions(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok("Permisos consultados", profileService.getPermissions(authentication)));
    }
}
