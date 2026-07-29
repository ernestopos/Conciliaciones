package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.commissionReconciliation;

import com.conciliaciones.reconciliation.core.application.port.in.commissionReconciliation.GenerateCommissionPaymentUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.commissionReconciliation.GetCommissionReconciliationByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.commissionReconciliation.ListCommissionReconciliationsUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionReconciliation.CommissionReconciliationResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.commissionReconciliation.GenerateCommissionPaymentRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/core/v1/commission-reconciliations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Commission Reconciliations", description = "Consulta y generación de pagos desde conciliaciones de comisiones")
public class CommissionReconciliationController {

    private final ListCommissionReconciliationsUseCase listCommissionReconciliationsUseCase;
    private final GetCommissionReconciliationByIdUseCase getCommissionReconciliationByIdUseCase;
    private final GenerateCommissionPaymentUseCase generateCommissionPaymentUseCase;

    @GetMapping
    public List<CommissionReconciliationResponse> list(
            @RequestParam(required = false) String producerName,
            @RequestParam(required = false) String policyNumber,
            @RequestParam(required = false) String agencyName,
            @RequestParam(required = false) String carrierName
    ) {
        log.info("LOG INICIO X = listCommissionReconciliationsController");

        List<CommissionReconciliationResponse> response = listCommissionReconciliationsUseCase.list(
                producerName,
                policyNumber,
                agencyName,
                carrierName
        );

        log.info("LOG FIN X = listCommissionReconciliationsController total={}", response.size());
        return response;
    }

    @GetMapping("/{commissionStatementItemId}")
    public CommissionReconciliationResponse getById(@PathVariable Long commissionStatementItemId) {
        log.info("LOG INICIO X = getCommissionReconciliationByIdController id={}", commissionStatementItemId);
        CommissionReconciliationResponse response = getCommissionReconciliationByIdUseCase.getByCommissionStatementItemId(commissionStatementItemId);
        log.info("LOG FIN X = getCommissionReconciliationByIdController id={}", response.getCommissionStatementItemId());
        return response;
    }

    @PostMapping("/{commissionStatementItemId}/generate-payment")
    public CommissionReconciliationResponse generatePayment(
            @PathVariable Long commissionStatementItemId,
            @Valid @RequestBody GenerateCommissionPaymentRequest request
    ) {
        log.info("LOG INICIO X = generateCommissionPaymentFromReconciliationController id={}", commissionStatementItemId);

        CommissionReconciliationResponse response = generateCommissionPaymentUseCase.generatePayment(
                commissionStatementItemId,
                request
        );

        log.info("LOG FIN X = generateCommissionPaymentFromReconciliationController id={}", response.getCommissionStatementItemId());
        return response;
    }
}