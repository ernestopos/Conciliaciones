package com.conciliaciones.messaging.application.usecase;

import com.conciliaciones.messaging.application.port.in.PublishEventUseCase;
import com.conciliaciones.messaging.application.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublishEventService implements PublishEventUseCase {

    private final EventPublisherPort eventPublisherPort;

    @Override
    public void publish(String topic, String message) {
        eventPublisherPort.publishEvent(topic, message);
    }
}
