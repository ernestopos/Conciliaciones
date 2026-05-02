package com.conciliaciones.reconciliation.core.application.port.in.parameter;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.ParameterResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListParametersUseCase {
    Page<ParameterResponse> list(Pageable pageable);
}
