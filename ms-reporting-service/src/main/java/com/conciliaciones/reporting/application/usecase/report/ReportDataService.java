package com.conciliaciones.reporting.application.usecase.report;

import com.conciliaciones.persistence.projection.CommissionReportRow;
import com.conciliaciones.persistence.projection.PaymentMonthlyForCarrierReportRow;
import com.conciliaciones.persistence.repository.CommissionPaymentRepository;
import com.conciliaciones.reporting.domain.model.ReportRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.conciliaciones.reporting.infrastructure.adapter.in.rest.enums.KindReports;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportDataService {

    private static final int DEFAULT_LIMIT = 100;

    private final CommissionPaymentRepository commissionPaymentRepository;

    public List<Map<String, Object>> findRows(ReportRequest request) {

        return switch (KindReports.getKindReports(request.reportCode())) {
            case PaymentForEachProducer -> findPaymentForEachProducerRows(request);
            case PaymentMonthlyForCarrier -> paymentMonthlyCarriers(request);
            default -> List.of(Map.of(
                    "message", "Esqueleto de reporte generado correctamente",
                    "reportCode", request.reportCode()
            ));
        };
    }

    private List<Map<String, Object>> findPaymentForEachProducerRows(ReportRequest request) {
        int limit = resolveLimit(request.parameters());
        return commissionPaymentRepository.findCommissionReport(PageRequest.of(0, limit))
                .stream()
                .map(this::toMapCommissionReportRow)
                .toList();
    }

    private List<Map<String, Object>> paymentMonthlyCarriers(ReportRequest request) {
        Integer reportYear = resolveReportYear(request.parameters());
        return commissionPaymentRepository.findPaymentMonthlyForCarrierReport(reportYear)
                .stream()
                .map(this::toMapPaymentMonthlyRow)
                .toList();
    }

    private Integer resolveReportYear(Map<String, Object> parameters) {
        if (parameters == null || !parameters.containsKey("REPORT_YEAR")) {
            throw new IllegalArgumentException("El parámetro REPORT_YEAR es obligatorio.");
        }

        Object value = parameters.get("REPORT_YEAR");

        if (value instanceof Number number) {
            return number.intValue();
        }

        if (value instanceof String text && !text.isBlank()) {
            return Integer.parseInt(text.trim());
        }

        throw new IllegalArgumentException("El parámetro REPORT_YEAR no es válido.");
    }

    private Map<String, Object> toMapCommissionReportRow(CommissionReportRow row) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("producerName", row.producerName());
        values.put("policyNumber", row.policyNumber());
        values.put("policyAmount", row.policyAmount());
        values.put("commissionRate", row.commissionRate());
        values.put("producerRate", row.producerRate());
        values.put("commissionAmount", row.commissionAmount());
        values.put("paymentAmount", row.paymentAmount());
        return values;
    }

    private Map<String, Object> toMapPaymentMonthlyRow(PaymentMonthlyForCarrierReportRow row) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("carrierName", row.getCarrierName());
        values.put("agencyId", row.getAgencyId());
        values.put("agency", row.getAgency());
        values.put("producerId", row.getProducerId());
        values.put("producer", row.getProducer());
        values.put("january", row.getJanuary());
        values.put("february", row.getFebruary());
        values.put("march", row.getMarch());
        values.put("april", row.getApril());
        values.put("may", row.getMay());
        values.put("june", row.getJune());
        values.put("july", row.getJuly());
        values.put("august", row.getAugust());
        values.put("september", row.getSeptember());
        values.put("october", row.getOctober());
        values.put("november", row.getNovember());
        values.put("december", row.getDecember());
        values.put("total", row.getTotal());
        return values;
    }

    private int resolveLimit(Map<String, Object> parameters) {
        if (parameters == null || !parameters.containsKey("limit")) {
            return DEFAULT_LIMIT;
        }

        Object limit = parameters.get("limit");
        if (limit instanceof Number number) {
            return normalizeLimit(number.intValue());
        }
        if (limit instanceof String value && !value.isBlank()) {
            return normalizeLimit(Integer.parseInt(value.trim()));
        }
        return DEFAULT_LIMIT;
    }

    private int normalizeLimit(int value) {
        if (value <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(value, 500);
    }
}
