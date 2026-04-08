package com.conciliaciones.security.application.service;

import com.conciliaciones.security.api.request.AssignRolesRequest;
import com.conciliaciones.security.api.request.CreateUserRequest;
import com.conciliaciones.security.api.request.ResetPasswordRequest;
import com.conciliaciones.security.api.request.UpdateUserRequest;
import com.conciliaciones.security.api.request.UpdateUserStatusRequest;
import com.conciliaciones.security.api.response.UserResponse;
import com.conciliaciones.security.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    public UserResponse create(CreateUserRequest request) {
        return new UserResponse(
                UUID.randomUUID().toString(),
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName(),
                request.enabled(),
                true,
                request.roles()
        );
    }

    public List<UserResponse> findAll() {
        return List.of(
                new UserResponse(UUID.randomUUID().toString(), "admin-app", "admin-app@conciliaciones.local", "Admin", "App", true, true, List.of("ADMIN")),
                new UserResponse(UUID.randomUUID().toString(), "analyst", "analyst@conciliaciones.local", "Ana", "Lyst", true, true, List.of("ANALYST"))
        );
    }

    public UserResponse findById(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new UserNotFoundException("Usuario no encontrado");
        }
        return new UserResponse(userId, "admin-app", "admin-app@conciliaciones.local", "Admin", "App", true, true, List.of("ADMIN"));
    }

    public UserResponse update(String userId, UpdateUserRequest request) {
        return new UserResponse(userId, "admin-app", request.email(), request.firstName(), request.lastName(), request.enabled(), true, List.of("ADMIN"));
    }

    public UserResponse updateStatus(String userId, UpdateUserStatusRequest request) {
        return new UserResponse(userId, "admin-app", "admin-app@conciliaciones.local", "Admin", "App", request.enabled(), true, List.of("ADMIN"));
    }

    public void resetPassword(String userId, ResetPasswordRequest request) {
        // Stub: implementación real contra Keycloak Admin API.
    }

    public UserResponse assignRoles(String userId, AssignRolesRequest request) {
        return new UserResponse(userId, "admin-app", "admin-app@conciliaciones.local", "Admin", "App", true, true, request.roles());
    }
}
