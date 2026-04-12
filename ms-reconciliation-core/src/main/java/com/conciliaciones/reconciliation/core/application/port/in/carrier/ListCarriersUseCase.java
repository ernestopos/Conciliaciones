package com.conciliaciones.reconciliation.core.application.port.in.carrier;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier.CarrierResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListCarriersUseCase {
    Page<CarrierResponse> list(Pageable pageable);
}
