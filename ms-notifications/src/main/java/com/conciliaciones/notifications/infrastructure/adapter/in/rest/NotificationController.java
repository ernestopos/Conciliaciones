package com.conciliaciones.notifications.infrastructure.adapter.in.rest;

import com.conciliaciones.notifications.application.port.in.GetNotificationUseCase;
import com.conciliaciones.notifications.application.port.in.SendNotificationUseCase;
import com.conciliaciones.notifications.infrastructure.adapter.in.rest.dto.NotificationResponse;
import com.conciliaciones.notifications.infrastructure.adapter.in.rest.dto.SendNotificationRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Operaciones de notificaciones")
public class NotificationController {

    private final SendNotificationUseCase sendNotificationUseCase;
    private final GetNotificationUseCase getNotificationUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse send(@Valid @RequestBody SendNotificationRequest request) {
        return sendNotificationUseCase.send(request);
    }

    @GetMapping("/{id}")
    public NotificationResponse getById(@PathVariable Long id) {
        return getNotificationUseCase.getById(id);
    }
}
