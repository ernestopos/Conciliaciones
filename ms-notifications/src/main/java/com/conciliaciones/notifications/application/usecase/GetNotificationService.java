package com.conciliaciones.notifications.application.usecase;

import com.conciliaciones.notifications.application.port.in.GetNotificationUseCase;
import com.conciliaciones.notifications.application.port.out.NotificationPersistencePort;
import com.conciliaciones.notifications.infrastructure.adapter.in.rest.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetNotificationService implements GetNotificationUseCase {

    private final NotificationPersistencePort notificationPersistencePort;

    @Override
    public NotificationResponse getById(Long id) {
        var notification = notificationPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada: " + id));

        return new NotificationResponse(
                notification.getId(),
                notification.getChannel(),
                notification.getRecipient(),
                notification.getStatus().name()
        );
    }
}
