package com.conciliaciones.reconciliation.core.application.port.in.agency;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency.AgencyResponse;

public interface GetAgencyByIdUseCase {
    AgencyResponse getById(Long id);
}
