package com.conciliaciones.reconciliation.core.application.port.in.policy;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.PolicyResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.UpdatePolicyRequest;

public interface UpdatePolicyUseCase {
    PolicyResponse update(Long id, UpdatePolicyRequest request, String username);
}
