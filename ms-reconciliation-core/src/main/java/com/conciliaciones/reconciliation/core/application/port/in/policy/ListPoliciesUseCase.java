package com.conciliaciones.reconciliation.core.application.port.in.policy;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.PolicyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListPoliciesUseCase {
    Page<PolicyResponse> list(Pageable pageable);
}
