package com.conciliaciones.reconciliation.core.application.usecase.carrierPortal;

import com.conciliaciones.domain.entity.CarrierEntity;
import com.conciliaciones.domain.entity.CarrierPortalEntity;
import com.conciliaciones.reconciliation.core.application.port.in.carrierPortal.GetCarrierPortalByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.carrierPortal.ListCarrierPortalsUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.carrierPortal.CarrierPortalPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrierPortal.CarrierPortalResponse;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarrierPortalService
        implements ListCarrierPortalsUseCase, GetCarrierPortalByIdUseCase {

    private final CarrierPortalPersistencePort persistencePort;

    @Override
    public List<CarrierPortalResponse> listActive() {
        log.info("LOG INICIO X = listActiveCarrierPortals");
        List<CarrierPortalResponse> result = persistencePort.findActive()
                .stream()
                .map(this::toResponse)
                .toList();
        log.info("LOG FIN X = listActiveCarrierPortals total={}",result.size());
        return result;
    }

    @Override
    public CarrierPortalResponse getById(Long id) {
        log.info("LOG INICIO X = getCarrierPortalById id={}",id);
        CarrierPortalEntity entity = persistencePort.findById(id)
                .filter(portal -> Boolean.TRUE.equals(portal.getActive()))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Portal de aseguradora no encontrado con id: " + id
                        )
                );
        log.info("LOG FIN X = getCarrierPortalById id={}",           entity.getId()
        );
        return toResponse(entity);
    }

    private CarrierPortalResponse toResponse(CarrierPortalEntity entity) {
        CarrierEntity carrier = entity.getCarrier();
        return new CarrierPortalResponse(
                entity.getId(),
                carrier != null ? carrier.getId() : null,
                carrier != null ? carrier.getCode() : null,
                carrier != null ? carrier.getName() : null,
                entity.getCode(),
                entity.getDisplayName(),
                entity.getPortalUrl(),
                entity.getLogoUrl(),
                entity.getDescription(),
                entity.getAllowUpload(),
                entity.getAllowDownload(),
                entity.getRequiresMfa()
        );
    }
}