    package com.conciliaciones.reconciliation.core.application.usecase.carrier;

    import com.conciliaciones.domain.entity.CarrierEntity;
import com.conciliaciones.reconciliation.core.application.port.in.carrier.CreateCarrierUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.carrier.DeleteCarrierUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.carrier.GetCarrierByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.carrier.ListCarriersUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.carrier.UpdateCarrierUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.carrier.CarrierPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier.CarrierResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier.CreateCarrierRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier.UpdateCarrierRequest;
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
    public class CarrierService implements CreateCarrierUseCase, GetCarrierByIdUseCase, ListCarriersUseCase, UpdateCarrierUseCase, DeleteCarrierUseCase {

        private final CarrierPersistencePort persistencePort;

        @Override
        public CarrierResponse create(CreateCarrierRequest request, String username) {
            log.info("LOG INICIO X = createCarrier");

            CarrierEntity entity = CarrierEntity.builder()
                .code(request.code())
                .name(request.name())
                .description(request.description())
                .active(request.active())
                    .createdAt(LocalDateTime.now())
                    .createdBy(username)
                    .build();

            CarrierEntity saved = persistencePort.save(entity);
            log.info("LOG FIN X = createCarrier id={}", saved.getId());
            return toResponse(saved);
        }

        @Override
        public CarrierResponse getById(Long id) {
            log.info("LOG INICIO X = getCarrierById id={}", id);
            CarrierEntity entity = persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Carrier no encontrado con id: " + id));
            log.info("LOG FIN X = getCarrierById id={}", entity.getId());
            return toResponse(entity);
        }

        @Override
        public Page<CarrierResponse> list(Pageable pageable) {
            log.info("LOG INICIO X = listCarriers page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
            Page<CarrierResponse> result = persistencePort.findAll(pageable).map(this::toResponse);
            log.info("LOG FIN X = listCarriers totalElements={}", result.getTotalElements());
            return result;
        }

        @Override
        public CarrierResponse update(Long id, UpdateCarrierRequest request, String username) {
            log.info("LOG INICIO X = updateCarrier id={}", id);

            CarrierEntity entity = persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Carrier no encontrado con id: " + id));

        entity.setCode(request.code());
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setActive(request.active());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setUpdatedBy(username);

            CarrierEntity saved = persistencePort.save(entity);
            log.info("LOG FIN X = updateCarrier id={}", saved.getId());
            return toResponse(saved);
        }

        @Override
        public void delete(Long id) {
            log.info("LOG INICIO X = deleteCarrier id={}", id);
            persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Carrier no encontrado con id: " + id));
            persistencePort.deleteById(id);
            log.info("LOG FIN X = deleteCarrier id={}", id);
        }

        private CarrierResponse toResponse(CarrierEntity entity) {
            return new CarrierResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
            );
        }
    }
