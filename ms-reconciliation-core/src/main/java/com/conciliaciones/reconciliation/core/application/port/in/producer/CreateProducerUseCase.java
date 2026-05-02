package com.conciliaciones.reconciliation.core.application.port.in.producer;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer.CreateProducerRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer.ProducerResponse;

public interface CreateProducerUseCase {
    ProducerResponse create(CreateProducerRequest request, String username);
}
