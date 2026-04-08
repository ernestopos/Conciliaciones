package com.conciliaciones.security.api.controller;

import com.conciliaciones.security.api.response.AuditEventResponse;
import com.conciliaciones.security.application.service.AuditService;
import com.conciliaciones.security.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/security/audit")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Auditoría de seguridad")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @Operation(summary = "Listar eventos de auditoría")
    public ResponseEntity<ApiResponse<List<AuditEventResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Eventos consultados", auditService.findAll()));
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Consultar evento por id")
    public ResponseEntity<ApiResponse<AuditEventResponse>> findById(@PathVariable String eventId) {
        return ResponseEntity.ok(ApiResponse.ok("Evento consultado", auditService.findById(eventId)));
    }
}
