package com.conciliaciones.reconciliation.core.application.port.in.policy;

import com.conciliaciones.persistence.repository.projection.DonutCharValTolProjection;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.CreatePolicyRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.PolicyResponse;

import java.util.List;

public interface CreatePolicyUseCase {
    PolicyResponse create(CreatePolicyRequest request, String username);
    public List<DonutCharValTolProjection> charPolicyCreate();
}