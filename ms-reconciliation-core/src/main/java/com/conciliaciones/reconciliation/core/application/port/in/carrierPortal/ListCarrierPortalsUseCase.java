package com.conciliaciones.reconciliation.core.application.port.in.carrierPortal;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrierPortal.CarrierPortalResponse;

import java.util.List;

public interface ListCarrierPortalsUseCase {

    List<CarrierPortalResponse> listActive();
}