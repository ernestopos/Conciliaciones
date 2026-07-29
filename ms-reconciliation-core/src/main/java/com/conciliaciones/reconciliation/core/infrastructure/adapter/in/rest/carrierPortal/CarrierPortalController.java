package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.carrierPortal;

import com.conciliaciones.reconciliation.core.application.port.in.carrierPortal.GetCarrierPortalByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.carrierPortal.ListCarrierPortalsUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrierPortal.CarrierPortalResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/core/v1/carrier-portals")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Carrier Portal",
        description = "Consulta de portales transaccionales de aseguradoras"
)
public class CarrierPortalController {

    private final ListCarrierPortalsUseCase listCarrierPortalsUseCase;
    private final GetCarrierPortalByIdUseCase getCarrierPortalByIdUseCase;

    @GetMapping
    public List<CarrierPortalResponse> listActive() {
        log.info("LOG INICIO X = listActiveCarrierPortalsController");

        List<CarrierPortalResponse> response =
                listCarrierPortalsUseCase.listActive();

        log.info(
                "LOG FIN X = listActiveCarrierPortalsController total={}",
                response.size()
        );

        return response;
    }

    @GetMapping("/{id}")
    public CarrierPortalResponse getById(@PathVariable Long id) {
        log.info("LOG INICIO X = getCarrierPortalByIdController id={}",id);
        CarrierPortalResponse response = getCarrierPortalByIdUseCase.getById(id);
        log.info("LOG FIN X = getCarrierPortalByIdController id={}",response.id());
        return response;
    }
}