package com.conciliaciones.security.application.service;

import com.conciliaciones.security.api.response.AuthenticatedUserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProfileService {

    public AuthenticatedUserResponse getAuthenticatedUser(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "anonymous";
        return new AuthenticatedUserResponse(
                UUID.randomUUID().toString(),
                username,
                username + "@conciliaciones.local",
                "Usuario " + username,
                List.of("ADMIN"),
                List.of("USER_READ", "USER_WRITE", "ROLE_ASSIGN")
        );
    }

    public List<String> getRoles(Authentication authentication) {
        return List.of("ADMIN");
    }

    public List<String> getPermissions(Authentication authentication) {
        return List.of("USER_READ", "USER_WRITE", "ROLE_ASSIGN");
    }
}
