package com.conciliaciones.notifications.application.port.in;

import com.conciliaciones.notifications.infrastructure.adapter.in.rest.dto.NotificationResponse;
import com.conciliaciones.notifications.infrastructure.adapter.in.rest.dto.SendNotificationRequest;

public interface SendNotificationUseCase {
    NotificationResponse send(SendNotificationRequest request);
}
