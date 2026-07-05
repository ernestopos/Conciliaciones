package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.securityUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConfigureSecurityUserRequest(

        @NotBlank
        String username,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String fullName,

        Boolean active
) {
}