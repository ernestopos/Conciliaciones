package com.conciliaciones.mssecurity.infrastructure.adapter.in.rest;

import com.conciliaciones.mssecurity.application.port.in.ListKeycloakUsersUseCase;
import com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto.KeycloakUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/security/users")
@RequiredArgsConstructor
public class UserController {

    private final ListKeycloakUsersUseCase useCase;

    @GetMapping
    public ResponseEntity<List<KeycloakUserResponse>> findUsers() {
        return ResponseEntity.ok(useCase.findUsers());
    }
}
