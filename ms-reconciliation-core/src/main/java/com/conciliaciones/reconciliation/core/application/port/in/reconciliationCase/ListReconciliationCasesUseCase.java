package com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase;

import com.conciliaciones.persistence.repository.projection.DonutCharValTolProjection;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.ReconciliationCaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListReconciliationCasesUseCase {
    Page<ReconciliationCaseResponse> list(Pageable pageable);
    public List<DonutCharValTolProjection> charReconcilationCase();
}
