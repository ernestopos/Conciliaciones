package com.conciliaciones.reconciliation.core.application.port.in.client;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client.ClientResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client.UpdateClientRequest;

public interface UpdateClientUseCase {
    ClientResponse update(Long id, UpdateClientRequest request, String username);
}
