package com.conciliaciones.reporting.application.port.in;

import com.conciliaciones.reporting.domain.model.GeneratedReport;
import com.conciliaciones.reporting.domain.model.ReportRequest;

public interface GenerateReportUseCase {

    GeneratedReport generate(ReportRequest request);
}
