package com.conciliaciones.mssecurity.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.secure-browser")
public record SecureBrowserProperties(
        String idpInitiatedUrl,
        String portalUrl,
        boolean deepLinksEnabled
) {
}