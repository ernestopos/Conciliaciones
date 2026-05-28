package com.conciliaciones.notifications.application.port.out;

public interface EmailSenderPort {
    void sendEmail(String recipient, String subject, String message);
}
