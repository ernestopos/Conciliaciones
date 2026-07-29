package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.PolicyEntity;
import com.conciliaciones.persistence.repository.projection.DonutCharValTolProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PolicyRepository extends JpaRepository<PolicyEntity, Long>, JpaSpecificationExecutor<PolicyEntity> {
    @Query(value = """
        select par.value as statusName, count(par.value) as total
        from reconciliation.policy pol
        inner join reconciliation.parameter par on pol.status_id = par.id
        group by par.value
    """, nativeQuery = true)
    List<DonutCharValTolProjection> charPolicyCreate();
}