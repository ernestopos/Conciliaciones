package com.conciliaciones.reconciliation.core.application.usecase.policy;

import com.conciliaciones.domain.entity.CarrierEntity;
import com.conciliaciones.domain.entity.ClientEntity;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class PolicyService implements CreatePolicyUseCase, GetPolicyByIdUseCase, ListPoliciesUseCase, UpdatePolicyUseCase, DeletePolicyUseCase {

    private final PolicyPersistencePort persistencePort;
    private final CarrierPersistencePort carrierPersistencePort;
    private final ClientPersistencePort clientPersistencePort;
    private final ParameterPersistencePort parameterPersistencePort;

    @Override
    public PolicyResponse create(CreatePolicyRequest request, String username) {
        log.info("LOG INICIO X = createPolicy");

        validateRequiredRelations(request.carrierId(), request.statusId());
        validateClientExists(request.clientId());

        PolicyEntity entity = PolicyEntity.builder()
                .carrierId(request.carrierId())
                .clientId(request.clientId())
                .policyNumber(request.policyNumber())
                .subscriberId(request.subscriberId())
                .effectiveDate(request.effectiveDate())
                .issueDate(request.issueDate())
                .terminationDate(request.terminationDate())
                .statusId(request.statusId())
                .residentState(request.residentState())
                .issueState(request.issueState())
                .membersCount(request.membersCount())
                .sourceKey(request.sourceKey())
                .active(request.active())
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();

        PolicyEntity saved = persistencePort.save(entity);
        log.info("LOG FIN X = createPolicy id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public PolicyResponse getById(Long id) {
        log.info("LOG INICIO X = getPolicyById id={}", id);
        PolicyEntity entity = persistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy no encontrada con id: " + id));
        log.info("LOG FIN X = getPolicyById id={}", entity.getId());
        return toResponse(entity);
    }

    @Override
    public Page<PolicyResponse> list(Pageable pageable) {
        log.info("LOG INICIO X = listPolicies page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<PolicyResponse> result = persistencePort.findAll(pageable).map(this::toResponse);
        log.info("LOG FIN X = listPolicies totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public PolicyResponse update(Long id, UpdatePolicyRequest request, String username) {
        log.info("LOG INICIO X = updatePolicy id={}", id);

        PolicyEntity entity = persistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy no encontrada con id: " + id));

        validateRequiredRelations(request.carrierId(), request.statusId());
        validateClientExists(request.clientId());

        entity.setCarrierId(request.carrierId());
        entity.setClientId(request.clientId());
        entity.setPolicyNumber(request.policyNumber());
        entity.setSubscriberId(request.subscriberId());
        entity.setEffectiveDate(request.effectiveDate());
        entity.setIssueDate(request.issueDate());
        entity.setTerminationDate(request.terminationDate());
        entity.setStatusId(request.statusId());
        entity.setResidentState(request.residentState());
        entity.setIssueState(request.issueState());
        entity.setMembersCount(request.membersCount());
        entity.setSourceKey(request.sourceKey());
        entity.setActive(request.active());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(username);

        PolicyEntity saved = persistencePort.save(entity);
        log.info("LOG FIN X = updatePolicy id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        log.info("LOG INICIO X = deletePolicy id={}", id);
        persistencePort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy no encontrada con id: " + id));
        persistencePort.deleteById(id);
        log.info("LOG FIN X = deletePolicy id={}", id);
    }

    private void validateRequiredRelations(Long carrierId, Long statusId) {
        carrierPersistencePort.findById(carrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier no encontrado con id: " + carrierId));
        parameterPersistencePort.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("Estado de póliza no encontrado con id: " + statusId));
    }

    private void validateClientExists(Long clientId) {
        if (clientId == null) {
            return;
        }
        clientPersistencePort.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + clientId));
    }

    private PolicyResponse toResponse(PolicyEntity entity) {
        String carrierName = carrierPersistencePort.findById(entity.getCarrierId())
                .map(CarrierEntity::getName)
                .orElse(null);
        String clientName = entity.getClientId() == null ? null : clientPersistencePort.findById(entity.getClientId())
                .map(ClientEntity::getFullName)
                .orElse(null);
        String statusName = parameterPersistencePort.findById(entity.getStatusId())
                .map(ParameterEntity::getName)
                .orElse(null);

        return new PolicyResponse(
                entity.getId(),
                entity.getCarrierId(),
                carrierName,
                entity.getClientId(),
                clientName,
                entity.getPolicyNumber(),
                entity.getSubscriberId(),
                entity.getEffectiveDate(),
                entity.getIssueDate(),
                entity.getTerminationDate(),
                entity.getStatusId(),
                statusName,
                entity.getResidentState(),
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
