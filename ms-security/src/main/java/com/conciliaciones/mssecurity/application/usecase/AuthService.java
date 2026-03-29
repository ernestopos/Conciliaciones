package com.conciliaciones.mssecurity.application.usecase;

import com.conciliaciones.mssecurity.application.port.in.AuthUseCase;
import com.conciliaciones.mssecurity.application.port.in.AuditUseCase;
import com.conciliaciones.mssecurity.application.port.out.KeycloakPort;
import com.conciliaciones.mssecurity.domain.model.AuditActionResult;
import com.conciliaciones.mssecurity.domain.model.LoginResult;
import com.conciliaciones.mssecurity.domain.model.UserValidationResult;
import com.conciliaciones.mssecurity.infrastructure.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final KeycloakPort keycloakPort;
    private final AuditUseCase auditUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public LoginResult login(String username, String password) {
        log.info("LOG INICIO X = login");
        try {
            LoginResult result = keycloakPort.login(username, password);
            auditUseCase.register(username, "LOGIN", AuditActionResult.SUCCESS,
                    "Autenticación exitosa", null, Map.of("username", username), "ACTIVE");
            log.info("LOG FIN X = login");
            return result;
        } catch (RuntimeException ex) {
            auditUseCase.register(username, "LOGIN", AuditActionResult.FAILED,
                    "Error autenticando usuario", Map.of("username", username), null, "ERROR");
            log.error("Error durante login username={}", username, ex);
            throw ex;
        }
    }

    @Override
    public UserValidationResult validate(String authorizationHeader) {
        log.info("LOG INICIO X = validate");
        String accessToken = extractBearerToken(authorizationHeader);
        UserValidationResult result = keycloakPort.validateToken(accessToken);
        auditUseCase.register(result.username(), "VALIDATE_TOKEN", AuditActionResult.SUCCESS,
                "Validación de token ejecutada", null, result, result.active() ? "ACTIVE" : "INACTIVE");
        log.info("LOG FIN X = validate");
        return result;
    }

    @Override
    public List<String> getRoles(String authorizationHeader) {
        log.info("LOG INICIO X = getRoles");
        String accessToken = extractBearerToken(authorizationHeader);
        List<String> roles = extractRolesFromJwt(accessToken);
        auditUseCase.register("NA", "VALIDATE_ROLES", AuditActionResult.SUCCESS,
                "Consulta de roles", null, roles, "ACTIVE");
        log.info("LOG FIN X = getRoles");
        return roles;
    }

    private String extractBearerToken(String authorizationHeader) {
        log.info("LOG INICIO X = extractBearerToken");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("Header Authorization inválido");
        }
        String token = authorizationHeader.substring(7);
        log.info("LOG FIN X = extractBearerToken");
        return token;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRolesFromJwt(String jwt) {
        log.info("LOG INICIO X = extractRolesFromJwt");
        try {
            String[] chunks = jwt.split("\\.");
            if (chunks.length < 2) {
                log.info("LOG FIN X = extractRolesFromJwt");
            return Collections.emptyList();
            }
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]), StandardCharsets.UTF_8);
            Map<String, Object> payloadMap = objectMapper.readValue(payload, Map.class);
            Map<String, Object> realmAccess = (Map<String, Object>) payloadMap.getOrDefault("realm_access", Collections.emptyMap());
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof List<?> roles) {
                List<String> result = roles.stream().map(String::valueOf).toList();
                log.info("LOG FIN X = extractRolesFromJwt");
                return result;
            }
            log.info("LOG FIN X = extractRolesFromJwt");
            return Collections.emptyList();
        } catch (Exception ex) {
            log.error("Error extrayendo roles desde token", ex);
            throw new AuthenticationException("No fue posible obtener roles del token");
        }
    }
}
