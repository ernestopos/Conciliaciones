package com.conciliaciones.mssecurity.infrastructure.adapter.out.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserInfoResponse(
        @JsonProperty("preferred_username") String preferredUsername,
        @JsonProperty("email_verified") Boolean emailVerified,
        @JsonProperty("sub") String sub
) {
}
