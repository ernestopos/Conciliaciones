    package com.conciliaciones.reconciliation.core.application.usecase.parameter;

    import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.reconciliation.core.application.port.in.parameter.CreateParameterUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.parameter.DeleteParameterUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.parameter.GetParameterByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.parameter.ListParametersUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.parameter.UpdateParameterUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.parameter.ParameterPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.CreateParameterRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.ParameterResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.parameter.UpdateParameterRequest;
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
    public class ParameterService implements CreateParameterUseCase, GetParameterByIdUseCase, ListParametersUseCase, UpdateParameterUseCase, DeleteParameterUseCase {

        private final ParameterPersistencePort persistencePort;

        @Override
        public ParameterResponse create(CreateParameterRequest request, String username) {
            log.info("LOG INICIO X = createParameter");

            ParameterEntity entity = ParameterEntity.builder()
                .name(request.name())
                .description(request.description())
                .value(request.value())
                .parameterGroup(request.parameterGroup())
                .active(request.active())
                .sortOrder(request.sortOrder())
                    .createdAt(LocalDateTime.now())
                    .createdBy(username)
                    .build();

            ParameterEntity saved = persistencePort.save(entity);
            log.info("LOG FIN X = createParameter id={}", saved.getId());
            return toResponse(saved);
        }

        @Override
        public ParameterResponse getById(Long id) {
            log.info("LOG INICIO X = getParameterById id={}", id);
            ParameterEntity entity = persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Parameter no encontrado con id: " + id));
            log.info("LOG FIN X = getParameterById id={}", entity.getId());
            return toResponse(entity);
        }

        @Override
        public Page<ParameterResponse> list(Pageable pageable) {
            log.info("LOG INICIO X = listParameters page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
            Page<ParameterResponse> result = persistencePort.findAll(pageable).map(this::toResponse);
            log.info("LOG FIN X = listParameters totalElements={}", result.getTotalElements());
            return result;
        }

        @Override
        public ParameterResponse update(Long id, UpdateParameterRequest request, String username) {
            log.info("LOG INICIO X = updateParameter id={}", id);

            ParameterEntity entity = persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Parameter no encontrado con id: " + id));

        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setValue(request.value());
        entity.setParameterGroup(request.parameterGroup());
        entity.setActive(request.active());
        entity.setSortOrder(request.sortOrder());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setUpdatedBy(username);

            ParameterEntity saved = persistencePort.save(entity);
            log.info("LOG FIN X = updateParameter id={}", saved.getId());
            return toResponse(saved);
        }

        @Override
        public void delete(Long id) {
            log.info("LOG INICIO X = deleteParameter id={}", id);
            persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Parameter no encontrado con id: " + id));
            persistencePort.deleteById(id);
            log.info("LOG FIN X = deleteParameter id={}", id);
        }

        private ParameterResponse toResponse(ParameterEntity entity) {
            return new ParameterResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getValue(),
                entity.getParameterGroup(),
                entity.getActive(),
                entity.getSortOrder(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
            );
        }
    }
