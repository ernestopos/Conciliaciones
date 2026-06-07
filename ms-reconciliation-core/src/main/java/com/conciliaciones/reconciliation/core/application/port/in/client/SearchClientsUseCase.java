package com.conciliaciones.reconciliation.core.application.port.in.client;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.client.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchClientsUseCase {

    Page<ClientResponse> search(String externalClientId, String fullName, Pageable pageable);
}