package com.conciliaciones.reconciliation.core.application.port.in.carrier;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier.CreateCarrierRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier.CarrierResponse;

public interface CreateCarrierUseCase {
    CarrierResponse create(CreateCarrierRequest request, String username);
}
