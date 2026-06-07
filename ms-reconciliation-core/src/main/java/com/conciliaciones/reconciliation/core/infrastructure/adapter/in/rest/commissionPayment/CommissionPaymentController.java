package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.commissionPayment;

import com.conciliaciones.reconciliation.core.application.port.in.commissionPayment.ListCommissionPaymentsUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment.CommissionPaymentResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.conciliaciones.reconciliation.core.application.port.in.commissionPayment.GetCommissionPaymentByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.commissionPayment.RecalculateCommissionPaymentUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionPayment.RecalculateCommissionPaymentRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/commission-payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Commission Payments", description = "Consulta de pagos de comisiones")
public class CommissionPaymentController {

    private final ListCommissionPaymentsUseCase listCommissionPaymentsUseCase;
    private final GetCommissionPaymentByIdUseCase getCommissionPaymentByIdUseCase;
    private final RecalculateCommissionPaymentUseCase recalculateCommissionPaymentUseCase;

    @GetMapping
    public List<CommissionPaymentResponse> list(
            @RequestParam(required = false) String producerName,
            @RequestParam(required = false) String policyNumber,
            @RequestParam(required = false) String agencyName,
            @RequestParam(required = false) String carrierName
    ) {
        log.info("LOG INICIO X = listCommissionPaymentsController");

        List<CommissionPaymentResponse> response = listCommissionPaymentsUseCase.list(
                producerName,
                policyNumber,
                agencyName,
                carrierName
        );

        log.info("LOG FIN X = listCommissionPaymentsController total={}", response.size());
        return response;
    }

    @GetMapping("/{id}")
    public CommissionPaymentResponse getById(@PathVariable Long id) {
        log.info("LOG INICIO X = getCommissionPaymentByIdController id={}", id);

        CommissionPaymentResponse response = getCommissionPaymentByIdUseCase.getById(id);

        log.info("LOG FIN X = getCommissionPaymentByIdController id={}", response.id());
        return response;
    }

    @PutMapping("/{id}/recalculate")
    public CommissionPaymentResponse recalculate(@PathVariable Long id,@Valid @RequestBody RecalculateCommissionPaymentRequest request) {
        log.info("LOG INICIO X = recalculateCommissionPaymentController id={}", id);
        CommissionPaymentResponse response = recalculateCommissionPaymentUseCase.recalculate(id,request);
        log.info("LOG FIN X = recalculateCommissionPaymentController id={}", response.id());
        return response;
    }
}