package com.conciliaciones.mssecurity.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;

public record SecureBrowserLaunchRequest(
        @NotNull(message = "El portal de la aseguradora es obligatorio")
        Long carrierPortalId
) {
}