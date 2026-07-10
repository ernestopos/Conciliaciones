package com.conciliaciones.mssecurity.infrastructure.adapter.out.keycloak;

import com.conciliaciones.mssecurity.application.port.out.KeycloakPort;
import com.conciliaciones.mssecurity.domain.model.LoginResult;
import com.conciliaciones.mssecurity.domain.model.UserValidationResult;
import com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto.KeycloakUserResponse;
import com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto.RoleRepresentation;
import com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto.UserRepresentation;
import com.conciliaciones.mssecurity.infrastructure.config.KeycloakProperties;
import com.conciliaciones.mssecurity.infrastructure.exception.AuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakClientAdapter implements KeycloakPort {

    private final RestClient keycloakRestClient;
    private final KeycloakProperties keycloakProperties;

    @Override
    @Retry(name = "keycloakClient")
    @CircuitBreaker(name = "keycloakClient")
    public LoginResult login(String username, String password) {
        log.info("LOG INICIO X = login");
        log.info(
                "Consumiento token Keycloak. serverUrl={}, realm={}, clientId={}, username={}",
                keycloakProperties.serverUrl(),
                keycloakProperties.realm(),
                keycloakProperties.clientId(),
                username
        );

        try {
            MultiValueMap<String, String> payload = new LinkedMultiValueMap<>();
            payload.add("grant_type", "password");
            payload.add("client_id", keycloakProperties.clientId());
            payload.add("username", username);
            payload.add("password", password);

            /*
             * Solo enviar client_secret si realmente el cliente es confidential
             * y el secret aplica. Para admin-cli normalmente NO se envía.
             */
            if (shouldSendClientSecret()) {
                payload.add("client_secret", keycloakProperties.clientSecret());
            }

            TokenResponse response = keycloakRestClient.post()
                    .uri("/realms/{realm}/protocol/openid-connect/token", keycloakProperties.realm())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(TokenResponse.class);

            if (response == null || response.accessToken() == null || response.accessToken().isBlank()) {
                log.error("Keycloak respondió sin access token. realm={}, clientId={}, username={}",
                        keycloakProperties.realm(), keycloakProperties.clientId(), username);
                throw new AuthenticationException("Keycloak no retornó token");
            }

            log.info("LOG FIN X = login");

            List<String> roles = findUserRoles(username);

            log.info("=========================================================");
            log.info("Roles extraídos del JWT: {}", roles);
            log.info("=========================================================");

            return new LoginResult(
                    response.accessToken(),
                    response.refreshToken(),
                    response.tokenType(),
                    response.expiresIn(),
                    roles
            );

        } catch (HttpClientErrorException.Unauthorized ex) {
            log.error(
                    "401 Unauthorized consumiendo login de Keycloak. realm={}, clientId={}, username={}, response={}",
                    keycloakProperties.realm(),
                    keycloakProperties.clientId(),
                    username,
                    ex.getResponseBodyAsString(),
                    ex
            );
            throw new AuthenticationException("Credenciales inválidas o cliente no autorizado en Keycloak");
        } catch (HttpClientErrorException.BadRequest ex) {
            log.error(
                    "400 Bad Request consumiendo login de Keycloak. realm={}, clientId={}, username={}, response={}",
                    keycloakProperties.realm(),
                    keycloakProperties.clientId(),
                    username,
                    ex.getResponseBodyAsString(),
                    ex
            );
            throw new AuthenticationException("Solicitud inválida hacia Keycloak");
        } catch (HttpClientErrorException ex) {
            log.error(
                    "Error HTTP consumiendo login de Keycloak. status={}, realm={}, clientId={}, username={}, response={}",
                    ex.getStatusCode(),
                    keycloakProperties.realm(),
                    keycloakProperties.clientId(),
                    username,
                    ex.getResponseBodyAsString(),
                    ex
            );
            throw new AuthenticationException("No fue posible autenticar contra Keycloak");
        } catch (Exception ex) {
            log.error(
                    "Error inesperado consumiendo login de Keycloak. realm={}, clientId={}, username={}",
                    keycloakProperties.realm(),
                    keycloakProperties.clientId(),
                    username,
                    ex
            );
            throw new AuthenticationException("No fue posible autenticar contra Keycloak");
        }
    }

    private List<String> findUserRoles(String username) {
        String adminToken = obtainAdminToken();
        UserRepresentation[] users =
                keycloakRestClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/admin/realms/{realm}/users")
                                .queryParam("username", username)
                                .queryParam("exact", true)
                                .build(keycloakProperties.realm()))
                        .header("Authorization", "Bearer " + adminToken)
                        .retrieve()
                        .body(UserRepresentation[].class);

        if (users == null || users.length == 0) {
            log.warn("No se encontró el usuario {} en Keycloak.", username);
            return List.of();
        }
        RoleRepresentation[] roles =
                keycloakRestClient.get()
                        .uri(
                                "/admin/realms/{realm}/users/{id}/role-mappings/realm",
                                keycloakProperties.realm(),
                                users[0].id())
                        .header("Authorization", "Bearer " + adminToken)
                        .retrieve()
                        .body(RoleRepresentation[].class);

        if (roles == null) {
            return List.of();
        }

        List<String> result = Arrays.stream(roles)
                .map(RoleRepresentation::name)
                .toList();

        log.info("Roles del usuario {}: {}", username, result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRolesFromJwt(String jwt) {
        try {
            String[] chunks = jwt.split("\\.");
            if (chunks.length < 2) {
                log.warn("JWT inválido.");
                return List.of();
            }
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]),StandardCharsets.UTF_8);
            log.info("=========== PAYLOAD JWT ===========");            log.info(payload);
            log.info("===================================");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payloadMap = mapper.readValue(payload, Map.class);
            log.info("Payload Map: {}", payloadMap);
            Map<String, Object> realmAccess = (Map<String, Object>) payloadMap.get("realm_access");

            log.info("Realm Access: {}", realmAccess);
            if (realmAccess == null) {
                log.warn("No existe realm_access dentro del token.");
                return List.of();
            }
            Object rolesObj = realmAccess.get("roles");
            log.info("Roles Object: {}", rolesObj);
            if (rolesObj instanceof List<?>) {
                List<String> roles = ((List<?>) rolesObj)
                        .stream()
                        .map(String::valueOf)
                        .toList();
                log.info("Roles encontrados: {}", roles);
                return roles;
            }

            log.warn("El atributo roles no es una lista.");
            return List.of();

        } catch (Exception ex) {
            log.error("Error leyendo JWT", ex);
            return List.of();
        }
    }

    @Override
    @Retry(name = "keycloakClient")
    @CircuitBreaker(name = "keycloakClient")
    public UserValidationResult validateToken(String accessToken) {
        log.info("LOG INICIO X = validateToken");

        try {
            UserInfoResponse response = keycloakRestClient.get()
                    .uri("/realms/{realm}/protocol/openid-connect/userinfo", keycloakProperties.realm())
                    .header("Authorization", "Bearer " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(UserInfoResponse.class);

            if (response == null || response.preferredUsername() == null || response.preferredUsername().isBlank()) {
                throw new AuthenticationException("Respuesta inválida de Keycloak");
            }

            UserValidationResult result = new UserValidationResult(
                    response.preferredUsername(),
                    true,
                    "Token válido"
            );

            log.info("LOG FIN X = validateToken");
            return result;

        } catch (HttpClientErrorException.Unauthorized ex) {
            log.error("401 Unauthorized validando token en Keycloak. response={}", ex.getResponseBodyAsString(), ex);
            throw new AuthenticationException("Token inválido o expirado");
        } catch (HttpClientErrorException ex) {
            log.error("Error HTTP validando token en Keycloak. status={}, response={}",
                    ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new AuthenticationException("Token inválido o expirado");
        } catch (Exception ex) {
            log.error("Error inesperado validando token en Keycloak", ex);
            throw new AuthenticationException("Token inválido o expirado");
        }
    }

    @Override
    public List<KeycloakUserResponse> findUsers() {

        String adminToken = obtainAdminToken();

        UserRepresentation[] users =
                keycloakRestClient.get()
                        .uri("/admin/realms/{realm}/users",
                                keycloakProperties.realm())
                        .header("Authorization",
                                "Bearer " + adminToken)
                        .retrieve()
                        .body(UserRepresentation[].class);

        if (users == null) {
            return List.of();
        }

        return Arrays.stream(users)
                .map(user -> {

                    RoleRepresentation[] roles =
                            keycloakRestClient.get()
                                    .uri(
                                            "/admin/realms/{realm}/users/{id}/role-mappings/realm",
                                            keycloakProperties.realm(),
                                            user.id())
                                    .header("Authorization",
                                            "Bearer " + adminToken)
                                    .retrieve()
                                    .body(RoleRepresentation[].class);

                    String role = null;

                    if (roles != null && roles.length > 0) {
                        role = roles[roles.length - 1].name();
                    }

                    return new KeycloakUserResponse(
                            user.id(),
                            user.username(),
                            user.email(),
                            user.firstName(),
                            user.lastName(),
                            user.enabled(),
                            role
                    );
                })
                .toList();
    }

    @Override
    @Retry(name = "keycloakClient")
    @CircuitBreaker(name = "keycloakClient")
    public LoginResult refresh(String refreshToken) {

        log.info("LOG INICIO X = refresh");

        try {
            MultiValueMap<String, String> payload = new LinkedMultiValueMap<>();
            payload.add("grant_type", "refresh_token");
            payload.add("client_id", keycloakProperties.clientId());
            payload.add("refresh_token", refreshToken);
            if (shouldSendClientSecret()) {
                payload.add("client_secret",keycloakProperties.clientSecret());
            }

            TokenResponse response = keycloakRestClient.post()
                    .uri("/realms/{realm}/protocol/openid-connect/token",keycloakProperties.realm())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(TokenResponse.class);

            if (response == null|| response.accessToken() == null|| response.accessToken().isBlank()) {
                throw new AuthenticationException("Keycloak no retornó un nuevo access token");
            }

            String newRefreshToken = response.refreshToken();

            if (newRefreshToken == null || newRefreshToken.isBlank()) {
                newRefreshToken = refreshToken;
            }

            List<String> roles = extractRolesFromJwt(response.accessToken());
            log.info("LOG FIN X = refresh");
            return new LoginResult(response.accessToken(),newRefreshToken,response.tokenType(),response.expiresIn(),roles);
        } catch (
                HttpClientErrorException.BadRequest
                | HttpClientErrorException.Unauthorized ex
        ) {

            log.warn(
                    "Refresh token inválido o expirado. status={}, response={}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString()
            );

            throw new AuthenticationException(
                    "La sesión expiró. Inicie sesión nuevamente"
            );

        } catch (HttpClientErrorException ex) {

            log.error(
                    "Error HTTP renovando token. status={}, response={}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString(),
                    ex
            );

            throw new AuthenticationException(
                    "No fue posible renovar la sesión"
            );

        } catch (AuthenticationException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error("Error inesperado renovando token", ex);

            throw new AuthenticationException(
                    "No fue posible renovar la sesión"
            );
        }
    }

    private String obtainAdminToken() {

        MultiValueMap<String, String> payload =
                new LinkedMultiValueMap<>();

        log.info("Admin client id usado: {}", keycloakProperties.adminClientId());
        log.info("Admin client secret configurado: {}", keycloakProperties.adminClientSecret() != null ? "SI" : "NO");
        payload.add("grant_type", "client_credentials");
        payload.add("client_id",keycloakProperties.adminClientId());
        payload.add("client_secret",keycloakProperties.adminClientSecret());

        TokenResponse response = keycloakRestClient.post()
                        .uri("/realms/{realm}/protocol/openid-connect/token",keycloakProperties.realm())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(payload)
                        .retrieve()
                        .body(TokenResponse.class);

        return response.accessToken();
    }

    private boolean shouldSendClientSecret() {
        return keycloakProperties.clientSecret() != null
                && !keycloakProperties.clientSecret().isBlank()
                && !"admin-cli".equalsIgnoreCase(keycloakProperties.clientId());
    }
}