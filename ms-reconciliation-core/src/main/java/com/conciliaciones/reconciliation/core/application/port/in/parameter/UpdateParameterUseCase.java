package com.conciliaciones.reconciliation.core.application.port.in.parameter;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.ParameterResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.UpdateParameterRequest;

public interface UpdateParameterUseCase {
    ParameterResponse update(Long id, UpdateParameterRequest request, String username);
}
