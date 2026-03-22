package com.conciliaciones.messaging.application.port.out;

public interface QueuePublisherPort {
    void sendMessage(String queue, String message);
}
