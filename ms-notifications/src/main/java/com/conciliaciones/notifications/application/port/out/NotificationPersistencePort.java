package com.conciliaciones.notifications.application.port.out;

import com.conciliaciones.notifications.domain.model.Notification;
import java.util.Optional;

public interface NotificationPersistencePort {
    Notification save(Notification notification);
    Optional<Notification> findById(Long id);
}
