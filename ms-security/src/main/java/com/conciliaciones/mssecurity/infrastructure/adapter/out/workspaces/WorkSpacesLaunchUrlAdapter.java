package com.conciliaciones.mssecurity.infrastructure.adapter.out.workspaces;

import com.conciliaciones.mssecurity.application.port.out.SecureBrowserLaunchUrlPort;
import com.conciliaciones.mssecurity.infrastructure.config.SecureBrowserProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class WorkSpacesLaunchUrlAdapter
        implements SecureBrowserLaunchUrlPort {

    private final SecureBrowserProperties properties;

    @Override
    public String build(String destinationUrl) {
        if (!properties.deepLinksEnabled()) {
            return properties.idpInitiatedUrl();
        }

        return buildDeepLink(destinationUrl);
    }

    private String buildDeepLink(String destinationUrl) {
        /*
         * Primera aproximación estructural.
         * El formato exacto debe validarse con el portal AWS
         * antes de activar deep-links-enabled=true.
         */
        return UriComponentsBuilder
                .fromUriString(properties.portalUrl())
                .queryParam("deepLinks", destinationUrl)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();
    }
}
