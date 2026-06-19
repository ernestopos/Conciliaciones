package com.conciliaciones.reporting.infrastructure.adapter.out.report;

import com.conciliaciones.reporting.application.usecase.report.ReportDataService;
import com.conciliaciones.reporting.domain.model.GeneratedReport;
import com.conciliaciones.reporting.domain.model.ReportRequest;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JasperReportService {

    private static final String JASPER_COMPILER_CLASS_PROPERTY = "net.sf.jasperreports.compiler.class";
    private static final String JDT_COMPILER_CLASS = "net.sf.jasperreports.jdt.JRJdtCompiler";

    private final ReportDataService reportDataService;

    public GeneratedReport generate(ReportRequest request) {
        try {
            System.setProperty(JASPER_COMPILER_CLASS_PROPERTY, JDT_COMPILER_CLASS);

            String templatePath = "/reports/" + request.reportCode() + ".jrxml";
            InputStream template = getClass().getResourceAsStream(templatePath);

            if (template == null) {
                throw new IllegalArgumentException("No existe plantilla Jasper para el reporte: " + request.reportCode());
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_TITLE", request.reportCode());
            parameters.put("FROM_DATE", request.fromDate());
            parameters.put("TO_DATE", request.toDate());

            if (request.parameters() != null) {
                parameters.putAll(request.parameters());
            }

            Collection<Map<String, ?>> rows = new ArrayList<>(reportDataService.findRows(request));

            JasperReport compiledReport = JasperCompileManager.compileReport(template);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    compiledReport,
                    parameters,
                    new JRMapCollectionDataSource(rows)
            );

            byte[] content = switch (request.format()) {
                case PDF -> JasperExportManager.exportReportToPdf(jasperPrint);
                case XLSX -> exportXlsx(jasperPrint);
            };

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = request.reportCode() + "-" + timestamp + "." + request.format().extension();

            return new GeneratedReport(fileName, request.format(), content);
        } catch (JRException ex) {
            throw new IllegalStateException("Error generando reporte Jasper", ex);
        }
    }

    private byte[] exportXlsx(JasperPrint jasperPrint) throws JRException {
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);

        exporter.setConfiguration(configuration);
        exporter.exportReport();

        return outputStream.toByteArray();
    }
}