package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.ReconciliationCaseEntity;
import com.conciliaciones.persistence.repository.projection.DonutCharValTolProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReconciliationCaseRepository extends JpaRepository<ReconciliationCaseEntity, Long> {
    @Modifying
    @Query(value = """
        UPDATE reconciliation.reconciliation_case
        SET status_id = :resolvedStatusId,
            resolved_at = :resolvedAt,
            updated_at = :resolvedAt
        WHERE commission_statement_item_id = :commissionStatementItemId
          AND status_id <> :resolvedStatusId
        """, nativeQuery = true)
    int resolveByCommissionStatementItemId(@Param("commissionStatementItemId") Long commissionStatementItemId,@Param("resolvedStatusId") Long resolvedStatusId,@Param("resolvedAt") LocalDateTime resolvedAt);

    @Query(value = """
        select p.value as statusName, count(p.value) as total
        from reconciliation.reconciliation_case rc
        inner join reconciliation.parameter p on p.id = rc.case_type_id
        group by p.value
    """, nativeQuery = true)
    List<DonutCharValTolProjection> charReconcilationCase();
}
