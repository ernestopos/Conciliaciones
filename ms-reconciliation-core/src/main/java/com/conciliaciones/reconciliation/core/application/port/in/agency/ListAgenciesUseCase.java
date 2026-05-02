package com.conciliaciones.reconciliation.core.application.port.in.agency;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency.AgencyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListAgenciesUseCase {
    Page<AgencyResponse> list(Pageable pageable);
}
