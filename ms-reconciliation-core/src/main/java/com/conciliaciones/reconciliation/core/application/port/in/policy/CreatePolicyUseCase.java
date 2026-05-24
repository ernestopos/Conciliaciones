package com.conciliaciones.reconciliation.core.application.port.in.policy;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.CreatePolicyRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.PolicyResponse;

public interface CreatePolicyUseCase {
    PolicyResponse create(CreatePolicyRequest request, String username);
}
