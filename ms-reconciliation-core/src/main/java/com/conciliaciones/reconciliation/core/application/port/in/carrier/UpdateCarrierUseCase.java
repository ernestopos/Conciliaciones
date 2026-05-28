package com.conciliaciones.reconciliation.core.application.port.in.carrier;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier.CarrierResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier.UpdateCarrierRequest;

public interface UpdateCarrierUseCase {
    CarrierResponse update(Long id, UpdateCarrierRequest request, String username);
}
