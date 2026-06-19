package com.conciliaciones.reconciliation.core.application.port.in.parameter;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.ParameterResponse;
import java.util.List;

public interface ListParametersByGroupUseCase {
    List<ParameterResponse> listByGroup(String parameterGroup);
}
