package com.conciliaciones.reconciliation.core.application.port.in.carrierPortal;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrierPortal.CarrierPortalResponse;

public interface GetCarrierPortalByIdUseCase {

    CarrierPortalResponse getById(Long id);
}