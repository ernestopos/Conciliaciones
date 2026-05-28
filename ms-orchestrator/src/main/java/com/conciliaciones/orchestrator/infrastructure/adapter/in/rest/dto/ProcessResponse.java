package com.conciliaciones.orchestrator.infrastructure.adapter.in.rest.dto;

public record ProcessResponse(
        Long id,
        String processCode,
        String businessCode,
        String status
) {
}
