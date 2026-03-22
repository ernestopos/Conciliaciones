package com.conciliaciones.notifications.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendNotificationRequest(
        @NotBlank String channel,
        @Email @NotBlank String recipient,
        @NotBlank String subject,
        @NotBlank String message
) {
}
