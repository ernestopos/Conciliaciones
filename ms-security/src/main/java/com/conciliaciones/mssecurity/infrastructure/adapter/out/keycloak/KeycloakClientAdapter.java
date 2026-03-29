package com.conciliaciones.mssecurity.infrastructure.adapter.out.keycloak;

import com.conciliaciones.mssecurity.application.port.out.KeycloakPort;
import com.conciliaciones.mssecurity.domain.model.LoginResult;
import com.conciliaciones.mssecurity.domain.model.UserValidationResult;
import com.conciliaciones.mssecurity.infrastructure.config.KeycloakProperties;
import com.conciliaciones.mssecurity.infrastructure.exception.AuthenticationException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

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
        try {
            MultiValueMap<String, String> payload = new LinkedMultiValueMap<>();
            payload.add("grant_type", keycloakProperties.grantType());
            payload.add("client_id", keycloakProperties.clientId());
            payload.add("client_secret", keycloakProperties.clientSecret());
            payload.add("username", username);
            payload.add("password", password);

            TokenResponse response = keycloakRestClient.post()
                    .uri("/realms/{realm}/protocol/openid-connect/token", keycloakProperties.realm())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(payload)
                    .retrieve()
                    .body(TokenResponse.class);

            if (response == null || response.accessToken() == null) {
                throw new AuthenticationException("Keycloak no retornó token");
            }
            log.info("LOG FIN X = login");
            return new LoginResult(response.accessToken(), response.refreshToken(), response.tokenType(),
                    response.expiresIn(), java.util.List.of());
        } catch (Exception ex) {
            log.error("Error consumiendo login de Keycloak username={}", username, ex);
            throw new AuthenticationException("No fue posible autenticar contra Keycloak");
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
                    .retrieve()
                    .body(UserInfoResponse.class);

            if (response == null || response.preferredUsername() == null) {
                throw new AuthenticationException("Respuesta inválida de Keycloak");
            }
            UserValidationResult result = new UserValidationResult(response.preferredUsername(), true, "Token válido");
            log.info("LOG FIN X = validateToken");
            return result;
        } catch (Exception ex) {
            log.error("Error validando token en Keycloak", ex);
            throw new AuthenticationException("Token inválido o expirado");
        }
    }
}
