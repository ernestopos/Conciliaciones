package com.conciliaciones.reconciliation.core.application.port.in.client;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client.CreateClientRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client.ClientResponse;

public interface CreateClientUseCase {
    ClientResponse create(CreateClientRequest request, String username);
}
