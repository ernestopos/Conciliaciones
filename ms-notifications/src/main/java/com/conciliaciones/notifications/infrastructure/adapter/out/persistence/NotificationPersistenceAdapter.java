package com.conciliaciones.notifications.infrastructure.adapter.out.persistence;

import com.conciliaciones.notifications.application.port.out.NotificationPersistencePort;
import com.conciliaciones.notifications.domain.model.Notification;
import com.conciliaciones.notifications.domain.model.NotificationStatus;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

/**
 * Cascarón base.
 * Luego se conectará con conciliaciones-persistence-jpa.
 */
@Component
public class NotificationPersistenceAdapter implements NotificationPersistencePort {

    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<Long, Notification> store = new ConcurrentHashMap<>();

    @Override
    public Notification save(Notification notification) {
        Long id = notification.getId() != null ? notification.getId() : sequence.incrementAndGet();

        Notification saved = Notification.builder()
                .id(id)
                .channel(notification.getChannel())
                .recipient(notification.getRecipient())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .status(notification.getStatus() != null ? notification.getStatus() : NotificationStatus.CREATED)
                .createdAt(notification.getCreatedAt())
                .build();

        store.put(id, saved);
        return saved;
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}
