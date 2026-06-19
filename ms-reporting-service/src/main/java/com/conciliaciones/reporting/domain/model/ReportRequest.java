package com.conciliaciones.reporting.domain.model;

import java.time.LocalDate;
import java.util.Map;

public record ReportRequest(
        String reportCode,
        ReportFormat format,
        LocalDate fromDate,
        LocalDate toDate,
        Map<String, Object> parameters
) {
}
