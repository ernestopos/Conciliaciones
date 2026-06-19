package com.conciliaciones.reporting.infrastructure.adapter.in.rest.dto;

import com.conciliaciones.reporting.domain.model.ReportFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;

public record GenerateReportRequest(
        @NotBlank String reportCode,
        @NotNull ReportFormat format,
        LocalDate fromDate,
        LocalDate toDate,
        Map<String, Object> parameters
) {
}
