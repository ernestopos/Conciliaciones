package com.conciliaciones.mssecurity.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.keycloak")
public record KeycloakProperties(
        String serverUrl,
        String realm,
        String clientId,
        String clientSecret,
        String grantType,
        int connectTimeoutMs,
        int readTimeoutMs
) {
}
