package com.conciliaciones.reconciliation.core.application.port.in.parameter;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.ParameterResponse;

public interface GetParameterByIdUseCase {
    ParameterResponse getById(Long id);
}
