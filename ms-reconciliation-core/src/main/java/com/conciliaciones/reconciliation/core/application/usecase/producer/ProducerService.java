    package com.conciliaciones.reconciliation.core.application.usecase.producer;

    import com.conciliaciones.domain.entity.ProducerEntity;
import com.conciliaciones.reconciliation.core.application.port.in.producer.CreateProducerUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.producer.DeleteProducerUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.producer.GetProducerByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.producer.ListProducersUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.producer.UpdateProducerUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.producer.ProducerPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer.CreateProducerRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer.ProducerResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.producer.UpdateProducerRequest;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

    @Service
    @Slf4j
    @RequiredArgsConstructor
    public class ProducerService implements CreateProducerUseCase, GetProducerByIdUseCase, ListProducersUseCase, UpdateProducerUseCase, DeleteProducerUseCase {

        private final ProducerPersistencePort persistencePort;

        @Override
        public ProducerResponse create(CreateProducerRequest request, String username) {
            log.info("LOG INICIO X = createProducer");

            ProducerEntity entity = ProducerEntity.builder()
                .agencyId(request.agencyId())
                .externalProducerId(request.externalProducerId())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .npn(request.npn())
                .taxIdMasked(request.taxIdMasked())
                .active(request.active())
                    .createdAt(LocalDateTime.now())
                    .createdBy(username)
                    .build();

            ProducerEntity saved = persistencePort.save(entity);
            log.info("LOG FIN X = createProducer id={}", saved.getId());
            return toResponse(saved);
        }

        @Override
        public ProducerResponse getById(Long id) {
            log.info("LOG INICIO X = getProducerById id={}", id);
            ProducerEntity entity = persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Producer no encontrado con id: " + id));
            log.info("LOG FIN X = getProducerById id={}", entity.getId());
            return toResponse(entity);
        }

        @Override
        public Page<ProducerResponse> list(Pageable pageable) {
            log.info("LOG INICIO X = listProducers page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
            Page<ProducerResponse> result = persistencePort.findAll(pageable).map(this::toResponse);
            log.info("LOG FIN X = listProducers totalElements={}", result.getTotalElements());
            return result;
        }

        @Override
        public ProducerResponse update(Long id, UpdateProducerRequest request, String username) {
            log.info("LOG INICIO X = updateProducer id={}", id);

            ProducerEntity entity = persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Producer no encontrado con id: " + id));

        entity.setAgencyId(request.agencyId());
        entity.setExternalProducerId(request.externalProducerId());
        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
        entity.setFullName(request.fullName());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setNpn(request.npn());
        entity.setTaxIdMasked(request.taxIdMasked());
        entity.setActive(request.active());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setUpdatedBy(username);

            ProducerEntity saved = persistencePort.save(entity);
            log.info("LOG FIN X = updateProducer id={}", saved.getId());
            return toResponse(saved);
        }

        @Override
        public void delete(Long id) {
            log.info("LOG INICIO X = deleteProducer id={}", id);
            persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Producer no encontrado con id: " + id));
            persistencePort.deleteById(id);
            log.info("LOG FIN X = deleteProducer id={}", id);
        }

        private ProducerResponse toResponse(ProducerEntity entity) {
            return new ProducerResponse(
                entity.getId(),
                entity.getAgencyId(),
                entity.getExternalProducerId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getNpn(),
                entity.getTaxIdMasked(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
            );
        }
    }
