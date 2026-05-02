package com.conciliaciones.reconciliation.core.application.port.in.producer;

import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer.ProducerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListProducersUseCase {
    Page<ProducerResponse> list(Pageable pageable);
}
