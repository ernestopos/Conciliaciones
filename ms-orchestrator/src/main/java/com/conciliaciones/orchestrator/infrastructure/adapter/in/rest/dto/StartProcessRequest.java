package com.conciliaciones.orchestrator.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record StartProcessRequest(
        @NotBlank String processCode,
        @NotBlank String businessCode
) {
}
