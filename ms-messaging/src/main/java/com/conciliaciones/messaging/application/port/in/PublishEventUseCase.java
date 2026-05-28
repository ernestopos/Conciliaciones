package com.conciliaciones.messaging.application.port.in;

public interface PublishEventUseCase {
    void publish(String topic, String message);
}
