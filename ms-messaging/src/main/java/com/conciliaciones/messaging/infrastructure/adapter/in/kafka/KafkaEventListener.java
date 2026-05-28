package com.conciliaciones.messaging.infrastructure.adapter.in.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventListener {

    @KafkaListener(topics = "test-topic")
    public void listen(String message) {
        System.out.println("Mensaje recibido Kafka: " + message);
    }
}
