package com.conciliaciones.notifications.application.port.in;

import com.conciliaciones.notifications.infrastructure.adapter.in.rest.dto.NotificationResponse;

public interface GetNotificationUseCase {
    NotificationResponse getById(Long id);
}
