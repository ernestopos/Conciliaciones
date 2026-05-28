package com.conciliaciones.reconciliation.core.application.port.in.producer;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer.ProducerResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer.UpdateProducerRequest;

public interface UpdateProducerUseCase {
    ProducerResponse update(Long id, UpdateProducerRequest request, String username);
}
