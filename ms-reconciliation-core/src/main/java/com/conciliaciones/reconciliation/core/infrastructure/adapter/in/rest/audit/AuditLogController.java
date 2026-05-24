package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.audit;

import com.conciliaciones.reconciliation.core.application.port.in.audit.GetAuditLogByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.audit.ListAuditLogsUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.audit.AuditLogResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Audit Log", description = "Consulta de registros de auditoría")
public class AuditLogController {

    private final ListAuditLogsUseCase listAuditLogsUseCase;
    private final GetAuditLogByIdUseCase getAuditLogByIdUseCase;

    @GetMapping
    public Page<AuditLogResponse> list(
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) Long actionId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable) {
        log.info("LOG INICIO X = listAuditLogsController page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<AuditLogResponse> response = listAuditLogsUseCase.list(entityName, entityId, actionId, username, from, to, pageable);
        log.info("LOG FIN X = listAuditLogsController totalElements={}", response.getTotalElements());
        return response;
    }

    @GetMapping("/{id}")
    public AuditLogResponse getById(@PathVariable Long id) {
        log.info("LOG INICIO X = getAuditLogByIdController id={}", id);
        AuditLogResponse response = getAuditLogByIdUseCase.getById(id);
        log.info("LOG FIN X = getAuditLogByIdController id={}", response.id());
        return response;
    }
}
