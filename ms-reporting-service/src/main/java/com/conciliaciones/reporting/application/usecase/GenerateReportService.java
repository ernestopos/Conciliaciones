package com.conciliaciones.reporting.application.usecase;

import com.conciliaciones.reporting.application.port.in.GenerateReportUseCase;
import com.conciliaciones.reporting.domain.model.GeneratedReport;
import com.conciliaciones.reporting.domain.model.ReportRequest;
import com.conciliaciones.reporting.infrastructure.adapter.out.report.JasperReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerateReportService implements GenerateReportUseCase {

    private final JasperReportService jasperReportService;

    @Override
    public GeneratedReport generate(ReportRequest request) {
        return jasperReportService.generate(request);
    }
}
