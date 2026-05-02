package com.conciliaciones.reconciliation.core.application.port.in.agency;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency.AgencyResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency.UpdateAgencyRequest;

public interface UpdateAgencyUseCase {
    AgencyResponse update(Long id, UpdateAgencyRequest request, String username);
}
