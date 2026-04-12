package com.conciliaciones.reconciliation.core.application.port.in.agency;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency.CreateAgencyRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency.AgencyResponse;

public interface CreateAgencyUseCase {
    AgencyResponse create(CreateAgencyRequest request, String username);
}
