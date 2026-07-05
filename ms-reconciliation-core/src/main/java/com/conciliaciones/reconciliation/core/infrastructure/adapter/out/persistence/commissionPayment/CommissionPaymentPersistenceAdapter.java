package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.commissionPayment;

import com.conciliaciones.persistence.repository.AgencyRepository;
import com.conciliaciones.persistence.repository.CommissionPaymentDetailRepository;
import com.conciliaciones.persistence.repository.CommissionStatementItemRepository;
import com.conciliaciones.persistence.repository.CommissionStatementRepository;
import com.conciliaciones.persistence.repository.PolicyRepository;
import com.conciliaciones.persistence.repository.ProducerRepository;
import com.conciliaciones.persistence.repository.projection.CommissionPaymentDetailView;
import com.conciliaciones.reconciliation.core.application.port.out.commissionPayment.CommissionPaymentPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import com.conciliaciones.domain.entity.AgencyEntity;
import com.conciliaciones.domain.entity.CommissionPaymentDetailEntity;
import com.conciliaciones.domain.entity.CommissionStatementEntity;
import com.conciliaciones.domain.entity.CommissionStatementItemEntity;
import com.conciliaciones.domain.entity.PolicyEntity;
import com.conciliaciones.domain.entity.ProducerEntity;
import java.util.Optional;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommissionPaymentPersistenceAdapter implements CommissionPaymentPersistencePort {

    private final CommissionPaymentDetailRepository repository;
    private final CommissionStatementRepository commissionStatementRepository;
    private final CommissionStatementItemRepository commissionStatementItemRepository;
    private final PolicyRepository policyRepository;
    private final ProducerRepository producerRepository;
    private final AgencyRepository agencyRepository;

    @Override
    public List<CommissionPaymentDetailView> findCommissionPayments(
            String producerName,
            String policyNumber,
            String agencyName,
            String carrierName
    ) {
        log.info("LOG INICIO X = findCommissionPaymentsPersistence");

        List<CommissionPaymentDetailView> result = repository.findCommissionPayments(
                normalize(producerName),
                normalize(policyNumber),
                normalize(agencyName),
                normalize(carrierName),
                PageRequest.of(0, 100)
        );

        log.info("LOG FIN X = findCommissionPaymentsPersistence total={}", result.size());
        return result;
    }

    private String normalize(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    @Override
    public Optional<CommissionPaymentDetailEntity> findById(Long id) {
        log.info("LOG INICIO X = findCommissionPaymentByIdPersistence id={}", id);

        Optional<CommissionPaymentDetailEntity> result = repository.findById(id);

        log.info("LOG FIN X = findCommissionPaymentByIdPersistence exists={}", result.isPresent());
        return result;
    }

    @Override
    public CommissionPaymentDetailEntity save(CommissionPaymentDetailEntity entity) {
        log.info("LOG INICIO X = saveCommissionPaymentPersistence id={}", entity.getId());

        CommissionPaymentDetailEntity saved = repository.save(entity);

        log.info("LOG FIN X = saveCommissionPaymentPersistence id={}", saved.getId());
        return saved;
    }
    @Override
    public CommissionStatementEntity saveStatement(CommissionStatementEntity entity) {
        log.info("LOG INICIO X = saveManualCommissionStatementPersistence policyId={}", entity.getPolicyId());
        CommissionStatementEntity saved = commissionStatementRepository.save(entity);
        log.info("LOG FIN X = saveManualCommissionStatementPersistence id={}", saved.getId());
        return saved;
    }

    @Override
    public CommissionStatementItemEntity saveItem(CommissionStatementItemEntity entity) {
        log.info("LOG INICIO X = saveManualCommissionStatementItemPersistence statementId={}", entity.getCommissionStatementId());
        CommissionStatementItemEntity saved = commissionStatementItemRepository.save(entity);
        log.info("LOG FIN X = saveManualCommissionStatementItemPersistence id={}", saved.getId());
        return saved;
    }

    @Override
    public Optional<PolicyEntity> findPolicyById(Long id) {
        return policyRepository.findById(id);
    }

    @Override
    public Optional<ProducerEntity> findProducerById(Long id) {
        return producerRepository.findById(id);
    }

    @Override
    public Optional<AgencyEntity> findAgencyById(Long id) {
        return agencyRepository.findById(id);
    }

    @Override
    public boolean existsPaymentForPolicy(Long policyId) {
        return repository.existsByPolicyId_Id(policyId);
    }

}
