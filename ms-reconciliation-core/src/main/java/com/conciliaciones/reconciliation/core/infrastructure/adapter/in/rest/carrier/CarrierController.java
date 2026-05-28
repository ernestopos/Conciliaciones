    package com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.carrier;

    import com.conciliaciones.reconciliation.core.application.port.in.carrier.CreateCarrierUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.carrier.DeleteCarrierUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.carrier.GetCarrierByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.carrier.ListCarriersUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.carrier.UpdateCarrierUseCase;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier.CarrierResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier.CreateCarrierRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.carrier.UpdateCarrierRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.support.AuthenticatedUserResolver;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/v1/carriers")
    @RequiredArgsConstructor
    @Slf4j
    @Tag(name = "Carrier", description = "Operaciones CRUD para Carrier")
    public class CarrierController {

        private final CreateCarrierUseCase createCarrierUseCase;
        private final GetCarrierByIdUseCase getCarrierByIdUseCase;
        private final ListCarriersUseCase listCarriersUseCase;
        private final UpdateCarrierUseCase updateCarrierUseCase;
        private final DeleteCarrierUseCase deleteCarrierUseCase;

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public CarrierResponse create(@Valid @RequestBody CreateCarrierRequest request, @AuthenticationPrincipal Jwt jwt) {
            log.info("LOG INICIO X = createCarrierController");
            CarrierResponse response = createCarrierUseCase.create(request, AuthenticatedUserResolver.resolveUsername(jwt));
            log.info("LOG FIN X = createCarrierController id={}", response.id());
            return response;
        }

        @GetMapping("/{id}")
        public CarrierResponse getById(@PathVariable Long id) {
            log.info("LOG INICIO X = getCarrierByIdController id={}", id);
            CarrierResponse response = getCarrierByIdUseCase.getById(id);
            log.info("LOG FIN X = getCarrierByIdController id={}", response.id());
            return response;
        }

        @GetMapping
        public Page<CarrierResponse> list(Pageable pageable) {
            log.info("LOG INICIO X = listCarriersController page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
            Page<CarrierResponse> response = listCarriersUseCase.list(pageable);
            log.info("LOG FIN X = listCarriersController totalElements={}", response.getTotalElements());
            return response;
        }

        @PutMapping("/{id}")
        public CarrierResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCarrierRequest request, @AuthenticationPrincipal Jwt jwt) {
            log.info("LOG INICIO X = updateCarrierController id={}", id);
            CarrierResponse response = updateCarrierUseCase.update(id, request, AuthenticatedUserResolver.resolveUsername(jwt));
            log.info("LOG FIN X = updateCarrierController id={}", response.id());
            return response;
        }

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void delete(@PathVariable Long id) {
            log.info("LOG INICIO X = deleteCarrierController id={}", id);
            deleteCarrierUseCase.delete(id);
            log.info("LOG FIN X = deleteCarrierController id={}", id);
        }

    }
