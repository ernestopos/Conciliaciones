package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.persistence.audit;

import com.conciliaciones.domain.entity.AuditLogEntity;
import com.conciliaciones.persistence.repository.AuditLogRepository;
import com.conciliaciones.reconciliation.core.application.port.out.audit.AuditLogPersistencePort;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLogPersistenceAdapter implements AuditLogPersistencePort {

    private final AuditLogRepository repository;

    @Override
    public Optional<AuditLogEntity> findById(Long id) {
        log.info("LOG INICIO X = findAuditLogByIdPersistence id={}", id);
        Optional<AuditLogEntity> result = repository.findById(id);
        log.info("LOG FIN X = findAuditLogByIdPersistence found={}", result.isPresent());
        return result;
    }

    @Override
    public Page<AuditLogEntity> findAll(String entityName, String entityId, Long actionId, String username,
                                        LocalDateTime from, LocalDateTime to, Pageable pageable) {
        log.info("LOG INICIO X = findAllAuditLogPersistence page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<AuditLogEntity> result = repository.findAll(buildSpecification(entityName, entityId, actionId, username, from, to), pageable);
        log.info("LOG FIN X = findAllAuditLogPersistence totalElements={}", result.getTotalElements());
        return result;
    }

    private Specification<AuditLogEntity> buildSpecification(String entityName, String entityId, Long actionId, String username,
                                                            LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(entityName)) {
                predicates.add(cb.like(cb.lower(root.get("entityName")), "%" + entityName.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(entityId)) {
                predicates.add(cb.like(cb.lower(root.get("entityId")), "%" + entityId.toLowerCase() + "%"));
            }
            if (actionId != null) {
                predicates.add(cb.equal(root.get("actionId"), actionId));
            }
            if (StringUtils.hasText(username)) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("eventTimestamp"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("eventTimestamp"), to));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
