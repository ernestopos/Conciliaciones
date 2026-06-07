package com.conciliaciones.reconciliation.core.application.usecase.policy;

import com.conciliaciones.domain.entity.ParameterEntity;
import com.conciliaciones.domain.entity.PolicyEntity;
import com.conciliaciones.reconciliation.core.application.port.in.policy.CreatePolicyUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.policy.DeletePolicyUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.policy.GetPolicyByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.policy.ListPoliciesUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.policy.UpdatePolicyUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.carrier.CarrierPersistencePort;
import com.conciliaciones.reconciliation.core.application.port.out.client.ClientPersistencePort;
import com.conciliaciones.reconciliation.core.application.port.out.parameter.ParameterPersistencePort;
import com.conciliaciones.reconciliation.core.application.port.out.policy.PolicyPersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.CreatePolicyRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.PolicyResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.policy.UpdatePolicyRequest;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.conciliaciones.domain.entity.CityEntity;
import com.conciliaciones.reconciliation.core.application.port.out.location.CityPersistencePort;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PolicyService implements CreatePolicyUseCase, GetPolicyByIdUseCase, ListPoliciesUseCase, UpdatePolicyUseCase, DeletePolicyUseCase {

    private final PolicyPersistencePort policyPersistencePort;
    private final CarrierPersistencePort carrierPersistencePort;
    private final ClientPersistencePort clientPersistencePort;
    private final ParameterPersistencePort parameterPersistencePort;
    private final CityPersistencePort cityPersistencePort;

    @Override
    public PolicyResponse create(CreatePolicyRequest request, String username) {
        log.info("LOG INICIO X = createPolicy");

        validateCarrier(request.carrierId());
        validateClient(request.clientId());

        ParameterEntity status = getParameter(request.statusId(), "Estado de póliza");
        CityEntity residentCity = getCity(request.residentCityId());

        PolicyEntity entity = PolicyEntity.builder()
                .carrierId(request.carrierId())
                .clientId(request.clientId())
                .policyNumber(request.policyNumber())
                .subscriberId(request.subscriberId())
                .effectiveDate(request.effectiveDate())
                .issueDate(request.issueDate())
                .terminationDate(request.terminationDate())
                .statusId(status)
                .residentState(residentCity)
                .issueState(request.issueState())
                .membersCount(request.membersCount())
                .sourceKey(request.sourceKey())
                .active(request.active())
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();

        PolicyEntity saved = policyPersistencePort.save(entity);

        log.info("LOG FIN X = createPolicy id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public PolicyResponse getById(Long id) {
        log.info("LOG INICIO X = getPolicyById id={}", id);

        PolicyEntity entity = policyPersistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada con id: " + id));

        log.info("LOG FIN X = getPolicyById id={}", entity.getId());
        return toResponse(entity);
    }

    @Override
    public Page<PolicyResponse> list(Pageable pageable) {
        log.info("LOG INICIO X = listPolicies page={} size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<PolicyResponse> result = policyPersistencePort.findAll(pageable).map(this::toResponse);

        log.info("LOG FIN X = listPolicies totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public PolicyResponse update(Long id, UpdatePolicyRequest request, String username) {
        log.info("LOG INICIO X = updatePolicy id={}", id);

        PolicyEntity entity = policyPersistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada con id: " + id));

        validateCarrier(request.carrierId());
        validateClient(request.clientId());

        ParameterEntity status = getParameter(request.statusId(), "Estado de póliza");
        CityEntity residentCity = getCity(request.residentCityId());

        entity.setCarrierId(request.carrierId());
        entity.setClientId(request.clientId());
        entity.setPolicyNumber(request.policyNumber());
        entity.setSubscriberId(request.subscriberId());
        entity.setEffectiveDate(request.effectiveDate());
        entity.setIssueDate(request.issueDate());
        entity.setTerminationDate(request.terminationDate());
        entity.setStatusId(status);
        entity.setResidentState(residentCity);
        entity.setIssueState(request.issueState());
        entity.setMembersCount(request.membersCount());
        entity.setSourceKey(request.sourceKey());
        entity.setActive(request.active());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(username);

        PolicyEntity saved = policyPersistencePort.save(entity);

        log.info("LOG FIN X = updatePolicy id={}", saved.getId());
        return toResponse(saved);
    }

    private CityEntity getCity(Long cityId) {
        return cityPersistencePort.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("Ciudad no encontrada con id: " + cityId));
    }

    @Override
    public void delete(Long id) {
        log.info("LOG INICIO X = deletePolicy id={}", id);

        policyPersistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada con id: " + id));

        policyPersistencePort.deleteById(id);

        log.info("LOG FIN X = deletePolicy id={}", id);
    }

    private void validateCarrier(Long carrierId) {
        carrierPersistencePort.findById(carrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier no encontrado con id: " + carrierId));
    }

    private void validateClient(Long clientId) {
        if (clientId == null) {
            return;
        }

        clientPersistencePort.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + clientId));
    }

    private ParameterEntity getParameter(Long parameterId, String fieldName) {
        return parameterPersistencePort.findById(parameterId)
                .orElseThrow(() -> new ResourceNotFoundException(fieldName + " no encontrado con id: " + parameterId));
    }

    private PolicyResponse toResponse(PolicyEntity entity) {
        ParameterEntity status = entity.getStatusId();
        CityEntity city = entity.getResidentState();

        return new PolicyResponse(
                entity.getId(),
                entity.getCarrierId(),
                entity.getClientId(),
                entity.getPolicyNumber(),
                entity.getSubscriberId(),
                entity.getEffectiveDate(),
                entity.getIssueDate(),
                entity.getTerminationDate(),
                status != null ? status.getId() : null,
                status != null ? status.getName() : null,
                city != null ? city.getId() : null,
                city != null ? city.getName() : null,
                city != null && city.getState() != null ? city.getState().getId() : null,
                city != null && city.getState() != null ? city.getState().getName() : null,
                city != null && city.getState() != null && city.getState().getCountry() != null ? city.getState().getCountry().getId() : null,
                city != null && city.getState() != null && city.getState().getCountry() != null ? city.getState().getCountry().getName() : null,
                entity.getIssueState(),
                entity.getMembersCount(),
                entity.getSourceKey(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }
}