package com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.ReconciliationCaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListReconciliationCasesUseCase {
    Page<ReconciliationCaseResponse> list(Pageable pageable);
}
