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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints de autenticación y autorización")
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    @Operation(
            summary = "Autenticar usuario contra Keycloak",
            description = "Recibe credenciales de usuario y retorna tokens de autenticación"
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Inicio autenticación de usuario");

        LoginResult result = authUseCase.login(request.username(), request.password());

        LoginResponse response = new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                result.tokenType(),
                result.expiresIn(),
                result.roles()
        );

        log.info("Autenticación exitosa de usuario");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validar token de acceso")
    public ResponseEntity<Map<String, Object>> validate(@AuthenticationPrincipal Jwt jwt) {
        log.info("LOG INICIO X = validate");
        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(Map.of(
                "username", jwt.getClaimAsString("preferred_username"),
                "active", true,
                "message", "Token válido"
        ));
        log.info("LOG FIN X = validate");
        return response;
    }

    @GetMapping("/roles")
    @Operation(
            summary = "Consultar roles del token",
            description = "Obtiene la lista de roles asociados al token JWT recibido"
    )
    public ResponseEntity<List<String>> roles(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("Inicio consulta de roles");

        List<String> roles = authUseCase.getRoles(authorizationHeader);

        log.info("Consulta de roles finalizada");
        return ResponseEntity.ok(roles);
    }
}