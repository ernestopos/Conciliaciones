package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CommissionPaymentDetailEntity;
import com.conciliaciones.persistence.repository.projection.CommissionPaymentDetailView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CommissionPaymentDetailRepository extends JpaRepository<CommissionPaymentDetailEntity, Long> {
    @Query(value = """
    SELECT
        cpd.id AS id,
        p.id AS policyId,
        p.policy_number AS policyName,
        cs.id AS commissionStatementId,
        pr.id AS producerId,
        pr.full_name AS producerName,
        a.id AS agencyId,
        a.name AS agencyName,
        ca.id AS carrierId,
        ca.name AS carrierName,
        csi.net_amount AS netAmount,
        csi.rate AS rate,
        csi.commission_rate_pct AS commissionRatePct,
        cpd.amount AS paymentAmount,
        cpd.included_for_payment AS includedForPayment,
        cpd.created_at AS createdAt
    FROM reconciliation.commission_payment_detail cpd
    JOIN reconciliation.policy p 
        ON p.id = cpd.policy_id
    JOIN reconciliation.commission_statement_item csi 
        ON csi.id = cpd.commission_statement_item_id
    JOIN reconciliation.commission_statement cs 
        ON cs.id = csi.commission_statement_id
    LEFT JOIN reconciliation.producer pr 
        ON pr.id = cs.producer_id
    LEFT JOIN reconciliation.agency a 
        ON a.id = cs.agency_id
    LEFT JOIN reconciliation.carrier ca 
        ON ca.id = cs.carrier_id
    WHERE (CAST(:producerName AS TEXT) IS NULL OR LOWER(pr.full_name) LIKE LOWER(CONCAT('%', CAST(:producerName AS TEXT), '%')))
      AND (CAST(:policyNumber AS TEXT) IS NULL OR LOWER(p.policy_number) LIKE LOWER(CONCAT('%', CAST(:policyNumber AS TEXT), '%')))
      AND (CAST(:agencyName AS TEXT) IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', CAST(:agencyName AS TEXT), '%')))
      AND (CAST(:carrierName AS TEXT) IS NULL OR LOWER(ca.name) LIKE LOWER(CONCAT('%', CAST(:carrierName AS TEXT), '%')))
    ORDER BY cpd.created_at DESC
    """, nativeQuery = true)
    List<CommissionPaymentDetailView> findCommissionPayments(
            @Param("producerName") String producerName,
            @Param("policyNumber") String policyNumber,
            @Param("agencyName") String agencyName,
            @Param("carrierName") String carrierName,
            Pageable pageable
    );

    @Query("""
        SELECT cpd
        FROM CommissionPaymentDetailEntity cpd
        JOIN FETCH cpd.commissionStatementItemId csi
        JOIN FETCH cpd.policyId p
        WHERE cpd.id = :id
        """)
    Optional<CommissionPaymentDetailEntity> findByIdWithCalculationData(@Param("id") Long id);
}