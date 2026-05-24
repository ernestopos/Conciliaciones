package com.conciliaciones.reconciliation.core.application.usecase.agency;

import com.conciliaciones.domain.entity.AgencyEntity;
import com.conciliaciones.domain.entity.CarrierEntity;
import com.conciliaciones.reconciliation.core.application.port.in.agency.CreateAgencyUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.agency.DeleteAgencyUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.agency.GetAgencyByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.agency.ListAgenciesUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.agency.UpdateAgencyUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.agency.AgencyPersistencePort;
import com.conciliaciones.reconciliation.core.application.port.out.carrier.CarrierPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency.AgencyResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency.CreateAgencyRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.agency.UpdateAgencyRequest;
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
public class AgencyService implements CreateAgencyUseCase, GetAgencyByIdUseCase, ListAgenciesUseCase, UpdateAgencyUseCase, DeleteAgencyUseCase {

    private final AgencyPersistencePort persistencePort;
    private final CarrierPersistencePort carrierPersistencePort;

    @Override
    public AgencyResponse create(CreateAgencyRequest request, String username) {
        log.info("LOG INICIO X = createAgency");

        validateCarrierExists(request.carrierId());

        AgencyEntity entity = AgencyEntity.builder()
                .carrierId(request.carrierId())
                .externalAgencyId(request.externalAgencyId())
                .name(request.name())
                .active(request.active())
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();

        AgencyEntity saved = persistencePort.save(entity);
        log.info("LOG FIN X = createAgency id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public AgencyResponse getById(Long id) {
        log.info("LOG INICIO X = getAgencyById id={}", id);
        AgencyEntity entity = persistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency no encontrado con id: " + id));
        log.info("LOG FIN X = getAgencyById id={}", entity.getId());
        return toResponse(entity);
    }

    @Override
    public Page<AgencyResponse> list(Pageable pageable) {
        log.info("LOG INICIO X = listAgencies page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<AgencyResponse> result = persistencePort.findAll(pageable).map(this::toResponse);
        log.info("LOG FIN X = listAgencies totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public AgencyResponse update(Long id, UpdateAgencyRequest request, String username) {
        log.info("LOG INICIO X = updateAgency id={}", id);

        AgencyEntity entity = persistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency no encontrado con id: " + id));

        validateCarrierExists(request.carrierId());

        entity.setCarrierId(request.carrierId());
        entity.setExternalAgencyId(request.externalAgencyId());
        entity.setName(request.name());
        entity.setActive(request.active());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(username);

        AgencyEntity saved = persistencePort.save(entity);
        log.info("LOG FIN X = updateAgency id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        log.info("LOG INICIO X = deleteAgency id={}", id);
        persistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agency no encontrado con id: " + id));
        persistencePort.deleteById(id);
        log.info("LOG FIN X = deleteAgency id={}", id);
    }

    private void validateCarrierExists(Long carrierId) {
        carrierPersistencePort.findById(carrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier no encontrado con id: " + carrierId));
    }

    private AgencyResponse toResponse(AgencyEntity entity) {
        String carrierName = carrierPersistencePort.findById(entity.getCarrierId())
                .map(CarrierEntity::getName)
                .orElse(null);

        return new AgencyResponse(
                entity.getId(),
                entity.getCarrierId(),
                carrierName,
                entity.getExternalAgencyId(),
                entity.getName(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }
}
