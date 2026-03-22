package com.conciliaciones.messaging.infrastructure.adapter.out.kafka;

import com.conciliaciones.messaging.application.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publishEvent(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
