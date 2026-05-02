    package com.conciliaciones.reconciliation.core.application.usecase.reconciliationCase;

    import com.conciliaciones.domain.entity.ReconciliationCaseEntity;
import com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase.CreateReconciliationCaseUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase.DeleteReconciliationCaseUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase.GetReconciliationCaseByIdUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase.ListReconciliationCasesUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase.ReprocessReconciliationCaseUseCase;
import com.conciliaciones.reconciliation.core.application.port.in.reconciliationCase.UpdateReconciliationCaseUseCase;
import com.conciliaciones.reconciliation.core.application.port.out.reconciliationCase.ReconciliationCasePersistencePort;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.CreateReconciliationCaseRequest;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.ReconciliationCaseResponse;
import com.conciliaciones.reconciliation.core.infrastructure.adapter.in.rest.dto.reconciliationCase.UpdateReconciliationCaseRequest;
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
    public class ReconciliationCaseService implements CreateReconciliationCaseUseCase, GetReconciliationCaseByIdUseCase, ListReconciliationCasesUseCase, UpdateReconciliationCaseUseCase, DeleteReconciliationCaseUseCase, ReprocessReconciliationCaseUseCase {

        private final ReconciliationCasePersistencePort persistencePort;

        @Override
        public ReconciliationCaseResponse create(CreateReconciliationCaseRequest request, String username) {
            log.info("LOG INICIO X = createReconciliationCase");

            ReconciliationCaseEntity entity = ReconciliationCaseEntity.builder()
                .sourceFileId(request.sourceFileId())
                .commissionStatementId(request.commissionStatementId())
                .commissionStatementItemId(request.commissionStatementItemId())
                .carrierId(request.carrierId())
                .policyId(request.policyId())
                .producerId(request.producerId())
                .caseTypeId(request.caseTypeId())
                .severityId(request.severityId())
                .statusId(request.statusId())
                .detectedAt(request.detectedAt())
                .description(request.description())
                .suggestedAction(request.suggestedAction())
                .resolutionNotes(request.resolutionNotes())
                .resolvedAt(request.resolvedAt())
                .resolvedBy(request.resolvedBy())
                    .createdAt(LocalDateTime.now())
                    .createdBy(username)
                    .build();

            ReconciliationCaseEntity saved = persistencePort.save(entity);
            log.info("LOG FIN X = createReconciliationCase id={}", saved.getId());
            return toResponse(saved);
        }

        @Override
        public ReconciliationCaseResponse getById(Long id) {
            log.info("LOG INICIO X = getReconciliationCaseById id={}", id);
            ReconciliationCaseEntity entity = persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("ReconciliationCase no encontrado con id: " + id));
            log.info("LOG FIN X = getReconciliationCaseById id={}", entity.getId());
            return toResponse(entity);
        }

        @Override
        public Page<ReconciliationCaseResponse> list(Pageable pageable) {
            log.info("LOG INICIO X = listReconciliationCases page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
            Page<ReconciliationCaseResponse> result = persistencePort.findAll(pageable).map(this::toResponse);
            log.info("LOG FIN X = listReconciliationCases totalElements={}", result.getTotalElements());
            return result;
        }

        @Override
        public ReconciliationCaseResponse update(Long id, UpdateReconciliationCaseRequest request, String username) {
            log.info("LOG INICIO X = updateReconciliationCase id={}", id);

            ReconciliationCaseEntity entity = persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("ReconciliationCase no encontrado con id: " + id));

        entity.setSourceFileId(request.sourceFileId());
        entity.setCommissionStatementId(request.commissionStatementId());
        entity.setCommissionStatementItemId(request.commissionStatementItemId());
        entity.setCarrierId(request.carrierId());
        entity.setPolicyId(request.policyId());
        entity.setProducerId(request.producerId());
        entity.setCaseTypeId(request.caseTypeId());
        entity.setSeverityId(request.severityId());
        entity.setStatusId(request.statusId());
        entity.setDetectedAt(request.detectedAt());
        entity.setDescription(request.description());
        entity.setSuggestedAction(request.suggestedAction());
        entity.setResolutionNotes(request.resolutionNotes());
        entity.setResolvedAt(request.resolvedAt());
        entity.setResolvedBy(request.resolvedBy());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setUpdatedBy(username);

            ReconciliationCaseEntity saved = persistencePort.save(entity);
            log.info("LOG FIN X = updateReconciliationCase id={}", saved.getId());
            return toResponse(saved);
        }

        @Override
        public void delete(Long id) {
            log.info("LOG INICIO X = deleteReconciliationCase id={}", id);
            persistencePort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("ReconciliationCase no encontrado con id: " + id));
            persistencePort.deleteById(id);
            log.info("LOG FIN X = deleteReconciliationCase id={}", id);
        }

@Override
public ReconciliationCaseResponse reprocess(Long id, String username) {
    log.info("LOG INICIO X = reprocessReconciliationCase id={}", id);

    ReconciliationCaseEntity entity = persistencePort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ReconciliationCase no encontrado con id: " + id));

    entity.setUpdatedAt(LocalDateTime.now());
    entity.setUpdatedBy(username);
    ReconciliationCaseEntity saved = persistencePort.save(entity);

    log.info("LOG FIN X = reprocessReconciliationCase id={}", saved.getId());
    return toResponse(saved);
}

        private ReconciliationCaseResponse toResponse(ReconciliationCaseEntity entity) {
            return new ReconciliationCaseResponse(
                entity.getId(),
                entity.getSourceFileId(),
                entity.getCommissionStatementId(),
                entity.getCommissionStatementItemId(),
                entity.getCarrierId(),
                entity.getPolicyId(),
                entity.getProducerId(),
                entity.getCaseTypeId(),
                entity.getSeverityId(),
                entity.getStatusId(),
                entity.getDetectedAt(),
                entity.getDescription(),
                entity.getSuggestedAction(),
                entity.getResolutionNotes(),
                entity.getResolvedAt(),
                entity.getResolvedBy(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
            );
        }
    }
