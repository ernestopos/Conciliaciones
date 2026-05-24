package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.policy;

import com.conciliaciones.domain.entity.PolicyEntity;
import com.conciliaciones.persistence.repository.PolicyRepository;
import com.conciliaciones.reconciliation.core.application.port.out.policy.PolicyPersistencePort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PolicyPersistenceAdapter implements PolicyPersistencePort {

    private final PolicyRepository repository;

    @Override
    public PolicyEntity save(PolicyEntity entity) {
        log.info("LOG INICIO X = savePolicyPersistence");
        PolicyEntity saved = repository.save(entity);
        log.info("LOG FIN X = savePolicyPersistence id={}", saved.getId());
        return saved;
    }

    @Override
    public Optional<PolicyEntity> findById(Long id) {
        log.info("LOG INICIO X = findPolicyByIdPersistence id={}", id);
        Optional<PolicyEntity> result = repository.findById(id);
        log.info("LOG FIN X = findPolicyByIdPersistence found={}", result.isPresent());
        return result;
    }

    @Override
    public Page<PolicyEntity> findAll(Pageable pageable) {
        log.info("LOG INICIO X = findAllPolicyPersistence");
        Page<PolicyEntity> result = repository.findAll(pageable);
        log.info("LOG FIN X = findAllPolicyPersistence totalElements={}", result.getTotalElements());
        return result;
    }

    @Override
    public void deleteById(Long id) {
        log.info("LOG INICIO X = deletePolicyPersistence id={}", id);
        repository.deleteById(id);
        log.info("LOG FIN X = deletePolicyPersistence id={}", id);
    }
}
