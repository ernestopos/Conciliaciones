package com.conciliaciones.reporting.application.usecase.report;

import com.conciliaciones.persistence.projection.CommissionReportRow;
import com.conciliaciones.persistence.repository.CommissionPaymentRepository;
import com.conciliaciones.reporting.domain.model.ReportRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportDataService {

    private static final String PAYMENT_FOR_EACH_PRODUCER = "PaymentForEachProducer";
    private static final int DEFAULT_LIMIT = 100;

    private final CommissionPaymentRepository commissionPaymentRepository;

    public List<Map<String, Object>> findRows(ReportRequest request) {
        if (PAYMENT_FOR_EACH_PRODUCER.equalsIgnoreCase(request.reportCode())) {
            return findPaymentForEachProducerRows(request);
        }

        return List.of(Map.of(
                "message", "Esqueleto de reporte generado correctamente",
                "reportCode", request.reportCode()
        ));
    }

    private List<Map<String, Object>> findPaymentForEachProducerRows(ReportRequest request) {
        int limit = resolveLimit(request.parameters());
        return commissionPaymentRepository.findCommissionReport(PageRequest.of(0, limit))
                .stream()
                .map(this::toMap)
                .toList();
    }

    private Map<String, Object> toMap(CommissionReportRow row) {
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
