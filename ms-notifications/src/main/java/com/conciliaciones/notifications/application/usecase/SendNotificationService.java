package com.conciliaciones.notifications.application.usecase;

import com.conciliaciones.notifications.application.port.in.SendNotificationUseCase;
import com.conciliaciones.notifications.application.port.out.EmailSenderPort;
import com.conciliaciones.notifications.application.port.out.NotificationPersistencePort;
import com.conciliaciones.notifications.domain.model.Notification;
import com.conciliaciones.notifications.domain.model.NotificationStatus;
import com.conciliaciones.notifications.infrastructure.adapter.in.rest.dto.NotificationResponse;
import com.conciliaciones.notifications.infrastructure.adapter.in.rest.dto.SendNotificationRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendNotificationService implements SendNotificationUseCase {

    private final NotificationPersistencePort notificationPersistencePort;
    private final EmailSenderPort emailSenderPort;

    @Override
    public NotificationResponse send(SendNotificationRequest request) {
        Notification saved = notificationPersistencePort.save(
                Notification.builder()
                        .channel(request.channel())
                        .recipient(request.recipient())
                        .subject(request.subject())
                        .message(request.message())
                        .status(NotificationStatus.CREATED)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        if ("EMAIL".equalsIgnoreCase(request.channel())) {
            emailSenderPort.sendEmail(request.recipient(), request.subject(), request.message());
        }

        return new NotificationResponse(
                saved.getId(),
                saved.getChannel(),
                saved.getRecipient(),
                saved.getStatus().name()
        );
    }
}
