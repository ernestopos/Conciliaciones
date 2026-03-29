package com.conciliaciones.mssecurity.infrastructure.adapter.in.rest;

import com.conciliaciones.mssecurity.application.port.in.AuthUseCase;
import com.conciliaciones.mssecurity.domain.model.LoginResult;
import com.conciliaciones.mssecurity.domain.model.UserValidationResult;
import com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto.LoginResponse;
import com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto.ValidateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints de autenticación y autorización")
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario contra Keycloak")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("LOG INICIO X = login");
        LoginResult result = authUseCase.login(request.username(), request.password());
        ResponseEntity<LoginResponse> response = ResponseEntity.ok(new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                result.tokenType(),
                result.expiresIn(),
                result.roles()));
        log.info("LOG FIN X = login");
        return response;
    }

    @GetMapping("/validate")
    @Operation(summary = "Validar token de acceso")
    public ResponseEntity<ValidateResponse> validate(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("LOG INICIO X = validate");
        UserValidationResult result = authUseCase.validate(authorizationHeader);
        ResponseEntity<ValidateResponse> response = ResponseEntity.ok(
                new ValidateResponse(result.username(), result.active(), result.message()));
        log.info("LOG FIN X = validate");
        return response;
    }

    @GetMapping("/roles")
    @Operation(summary = "Consultar roles del token")
    public ResponseEntity<List<String>> roles(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("LOG INICIO X = roles");
        ResponseEntity<List<String>> response = ResponseEntity.ok(authUseCase.getRoles(authorizationHeader));
        log.info("LOG FIN X = roles");
        return response;
    }
}
