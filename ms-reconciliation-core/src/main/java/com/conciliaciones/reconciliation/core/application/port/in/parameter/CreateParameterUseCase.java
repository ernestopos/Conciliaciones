package com.conciliaciones.reconciliation.core.application.port.in.parameter;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.CreateParameterRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.ParameterResponse;

public interface CreateParameterUseCase {
    ParameterResponse create(CreateParameterRequest request, String username);
}
