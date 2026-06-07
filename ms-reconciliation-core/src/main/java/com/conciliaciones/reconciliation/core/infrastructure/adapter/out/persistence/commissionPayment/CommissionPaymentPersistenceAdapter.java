package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.commissionPayment;

import com.conciliaciones.persistence.repository.CommissionPaymentDetailRepository;
import com.conciliaciones.persistence.repository.projection.CommissionPaymentDetailView;
import com.conciliaciones.reconciliation.core.application.port.out.commissionPayment.CommissionPaymentPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import com.conciliaciones.domain.entity.CommissionPaymentDetailEntity;
import java.util.Optional;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommissionPaymentPersistenceAdapter implements CommissionPaymentPersistencePort {

    private final CommissionPaymentDetailRepository repository;

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
}