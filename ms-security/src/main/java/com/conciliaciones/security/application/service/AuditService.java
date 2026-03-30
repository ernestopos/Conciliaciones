package com.conciliaciones.security.application.service;

import com.conciliaciones.security.api.response.AuditEventResponse;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    public List<AuditEventResponse> findAll() {
        return List.of(
                new AuditEventResponse(UUID.randomUUID().toString(), "LOGIN", "admin-app", "Inicio de sesión", "SUCCESS", "Autenticación exitosa", OffsetDateTime.now()),
                new AuditEventResponse(UUID.randomUUID().toString(), "USER_UPDATE", "admin-app", "Actualización de usuario", "SUCCESS", "Cambio de estado de usuario", OffsetDateTime.now())
        );
    }

    public AuditEventResponse findById(String eventId) {
        return new AuditEventResponse(eventId, "LOGIN", "admin-app", "Inicio de sesión", "SUCCESS", "Autenticación exitosa", OffsetDateTime.now());
    }
}
