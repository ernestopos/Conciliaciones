package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.support;

import org.springframework.security.oauth2.jwt.Jwt;

public final class AuthenticatedUserResolver {

    private AuthenticatedUserResolver() {
    }

    public static String resolveUsername(Jwt jwt) {
        if (jwt == null) {
            return "system";
        }
        if (jwt.getClaimAsString("preferred_username") != null) {
            return jwt.getClaimAsString("preferred_username");
        }
        if (jwt.getSubject() != null) {
            return jwt.getSubject();
        }
        return "system";
    }
}
