package com.conciliaciones.messaging.application.port.out;

public interface EventPublisherPort {
    void publishEvent(String topic, String message);
}
