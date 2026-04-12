package com.conciliaciones.reconciliation.core.application.port.in.client;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client.ClientResponse;

public interface GetClientByIdUseCase {
    ClientResponse getById(Long id);
}
