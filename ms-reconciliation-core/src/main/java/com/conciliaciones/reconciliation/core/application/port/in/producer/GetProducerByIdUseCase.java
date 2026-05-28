package com.conciliaciones.reconciliation.core.application.port.in.producer;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer.ProducerResponse;

public interface GetProducerByIdUseCase {
    ProducerResponse getById(Long id);
}
