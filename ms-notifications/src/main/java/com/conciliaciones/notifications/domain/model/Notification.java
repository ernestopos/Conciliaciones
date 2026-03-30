package com.conciliaciones.notifications.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Notification {
    private Long id;
    private String channel;
    private String recipient;
    private String subject;
    private String message;
    private NotificationStatus status;
    private LocalDateTime createdAt;
}
