package com.conciliaciones.security.application.service;

import com.conciliaciones.security.api.request.LoginRequest;
import com.conciliaciones.security.api.request.LogoutRequest;
import com.conciliaciones.security.api.request.RefreshTokenRequest;
import com.conciliaciones.security.api.response.AuthenticatedUserResponse;
import com.conciliaciones.security.api.response.LoginResponse;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    public LoginResponse login(LoginRequest request) {
        return new LoginResponse(
                "stub-access-token-for-" + request.username(),
                "stub-refresh-token-for-" + request.username(),
                "Bearer",
                300,
                1800,
                OffsetDateTime.now(),
                new AuthenticatedUserResponse(
                        UUID.randomUUID().toString(),
                        request.username(),
                        request.username() + "@conciliaciones.local",
                        "Usuario " + request.username(),
                        List.of("ADMIN"),
                        List.of("USER_READ", "USER_WRITE", "ROLE_ASSIGN")
                )
        );
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        return new LoginResponse(
                "refreshed-access-token",
                request.refreshToken(),
                "Bearer",
                300,
                1800,
                OffsetDateTime.now(),
                new AuthenticatedUserResponse(
                        UUID.randomUUID().toString(),
                        "admin-app",
                        "admin-app@conciliaciones.local",
                        "Admin App",
                        List.of("ADMIN"),
                        List.of("USER_READ", "USER_WRITE", "ROLE_ASSIGN")
                )
        );
    }

    public void logout(LogoutRequest request) {
        // Stub: en la implementación real se invocará el logout endpoint de Keycloak.
    }
}
