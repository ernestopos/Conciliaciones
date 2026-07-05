package com.conciliaciones.reporting.domain.model;

public record GeneratedReport(
        String fileName,
        ReportFormat format,
        byte[] content
) {
}
