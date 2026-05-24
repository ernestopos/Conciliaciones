package com.conciliaciones.reconciliation.core.application.port.out.policy;

import com.conciliaciones.domain.entity.PolicyEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PolicyPersistencePort {
    PolicyEntity save(PolicyEntity entity);
    Optional<PolicyEntity> findById(Long id);
    Page<PolicyEntity> findAll(Pageable pageable);
    void deleteById(Long id);
}
