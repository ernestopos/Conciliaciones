package com.conciliaciones.notifications.infrastructure.adapter.in.rest.dto;

public record NotificationResponse(
        Long id,
        String channel,
        String recipient,
        String status
) {
}
