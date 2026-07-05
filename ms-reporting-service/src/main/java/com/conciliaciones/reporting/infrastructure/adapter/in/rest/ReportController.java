package com.conciliaciones.reporting.infrastructure.adapter.in.rest;

import com.conciliaciones.reporting.application.port.in.GenerateReportUseCase;
import com.conciliaciones.reporting.domain.model.GeneratedReport;
import com.conciliaciones.reporting.domain.model.ReportRequest;
import com.conciliaciones.reporting.infrastructure.adapter.in.rest.dto.GenerateReportRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final GenerateReportUseCase generateReportUseCase;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ms-reporting-service OK");
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generate(@Valid @RequestBody GenerateReportRequest request) {
        GeneratedReport report = generateReportUseCase.generate(new ReportRequest(
                request.reportCode(),
                request.format(),
                request.fromDate(),
                request.toDate(),
                request.parameters()
        ));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(report.format().mediaType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.fileName() + "\"")
                .body(report.content());
    }
}
