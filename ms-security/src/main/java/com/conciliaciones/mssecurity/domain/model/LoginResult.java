package com.conciliaciones.mssecurity.domain.model;

import java.util.List;

public record LoginResult(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        List<String> roles
) {
}
