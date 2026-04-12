package com.conciliaciones.reconciliation.core.application.port.in.carrier;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier.CarrierResponse;

public interface GetCarrierByIdUseCase {
    CarrierResponse getById(Long id);
}
