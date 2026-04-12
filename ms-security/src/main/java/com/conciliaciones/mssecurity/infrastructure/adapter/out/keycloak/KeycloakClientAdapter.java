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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

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

            return new LoginResult(
                    response.accessToken(),
                    response.refreshToken(),
                    response.tokenType(),
                    response.expiresIn(),
                    List.of()
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

    private boolean shouldSendClientSecret() {
        return keycloakProperties.clientSecret() != null
                && !keycloakProperties.clientSecret().isBlank()
                && !"admin-cli".equalsIgnoreCase(keycloakProperties.clientId());
    }
}