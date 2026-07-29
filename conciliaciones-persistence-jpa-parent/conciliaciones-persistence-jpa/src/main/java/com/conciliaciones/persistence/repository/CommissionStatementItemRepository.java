package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CommissionStatementItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.conciliaciones.persistence.repository.projection.CommissionReconciliationView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommissionStatementItemRepository extends JpaRepository<CommissionStatementItemEntity, Long> {

    @Query(value = """
        SELECT
            csi.id AS commissionStatementItemId,
            cs.id AS commissionStatementId,        
            p.id AS policyId,
            p.policy_number AS policyNumber,
            ps.id AS policyStatusId,
            ps.name AS policyStatusName,        
            cl.id AS clientId,
            cl.full_name AS clientName,        
            pr.id AS producerId,
            pr.full_name AS producerName,        
            a.id AS agencyId,
            a.name AS agencyName,        
            ca.id AS carrierId,
            ca.name AS carrierName,        
            csi.net_amount AS netAmount,
            csi.rate AS rate,
            csi.commission_rate_pct AS commissionRatePct,        
            ROUND((csi.net_amount * (csi.rate / 100) * (csi.commission_rate_pct / 100)), 2) AS estimatedPaymentAmount,        
            CASE
                WHEN csi.net_amount < 0 THEN 'NET_AMOUNT_NEGATIVE'
                ELSE 'INVALID_POLICY_STATUS'
            END AS reconciliationType,        
            CASE
                WHEN csi.net_amount < 0 THEN 'Net Amount menor a cero'
                ELSE 'Estado de póliza no válido para pago'
            END AS reconciliationReason,        
            csi.created_at AS createdAt        
        FROM reconciliation.commission_statement_item csi
        JOIN reconciliation.commission_statement cs ON cs.id = csi.commission_statement_id
        JOIN reconciliation.policy p ON p.id = cs.policy_id
        JOIN reconciliation.parameter ps ON ps.id = p.status_id
        LEFT JOIN reconciliation.client cl ON cl.id = cs.client_id
        LEFT JOIN reconciliation.producer pr ON pr.id = cs.producer_id
        LEFT JOIN reconciliation.agency a ON a.id = cs.agency_id
        LEFT JOIN reconciliation.carrier ca ON ca.id = cs.carrier_id
        
        WHERE NOT EXISTS (
            SELECT 1
            FROM reconciliation.commission_payment_detail cpd
            WHERE cpd.commission_statement_item_id = csi.id
        )
        AND (
            csi.net_amount < 0
            OR (
                LOWER(ps.name) <> LOWER(:approvedStatus)
                AND LOWER(ps.value) <> LOWER(:approvedStatus)
            )
        )
        AND (CAST(:producerName AS TEXT) IS NULL OR LOWER(pr.full_name) LIKE LOWER(CONCAT('%', CAST(:producerName AS TEXT), '%')))
        AND (CAST(:policyNumber AS TEXT) IS NULL OR LOWER(p.policy_number) LIKE LOWER(CONCAT('%', CAST(:policyNumber AS TEXT), '%')))
        AND (CAST(:agencyName AS TEXT) IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', CAST(:agencyName AS TEXT), '%')))
        AND (CAST(:carrierName AS TEXT) IS NULL OR LOWER(ca.name) LIKE LOWER(CONCAT('%', CAST(:carrierName AS TEXT), '%')))        
        ORDER BY csi.created_at DESC
        """, nativeQuery = true)
    List<CommissionReconciliationView> findPendingCommissionReconciliationsBefore(@Param("approvedStatus") String approvedStatus,@Param("producerName") String producerName,@Param("policyNumber") String policyNumber,@Param("agencyName") String agencyName,@Param("carrierName") String carrierName,Pageable pageable);

    @Query(value = """
            SELECT
                csi.id AS commissionStatementItemId,
                cs.id AS commissionStatementId,
                p.id AS policyId,
                p.policy_number AS policyNumber,
                ps.id AS policyStatusId,
                ps.name AS policyStatusName,
                cl.id AS clientId,
                cl.full_name AS clientName,
                pr.id AS producerId,
                pr.full_name AS producerName,
                a.id AS agencyId,
                a.name AS agencyName,
                ca.id AS carrierId,
                ca.name AS carrierName,
                csi.net_amount AS netAmount,
                csi.rate AS rate,
                csi.commission_rate_pct AS commissionRatePct,
                ROUND(
                    (
                        csi.net_amount
                        * (csi.rate / 100)
                        * (csi.commission_rate_pct / 100)
                    ),
                    2
                ) AS estimatedPaymentAmount,
                case_type.name AS reconciliationType,
                rc.description AS reconciliationReason,
                csi.created_at AS createdAt
            
            FROM reconciliation.reconciliation_case rc
            
            JOIN reconciliation.commission_statement_item csi
                ON csi.id = rc.commission_statement_item_id
            
            JOIN reconciliation.commission_statement cs
                ON cs.id = rc.commission_statement_id
            
            JOIN reconciliation.policy p
                ON p.id = rc.policy_id
            
            JOIN reconciliation.parameter ps
                ON ps.id = p.status_id
            
            JOIN reconciliation.parameter case_type
                ON case_type.id = rc.case_type_id
            
            LEFT JOIN reconciliation.client cl
                ON cl.id = cs.client_id
            
            LEFT JOIN reconciliation.producer pr
                ON pr.id = cs.producer_id
            
            LEFT JOIN reconciliation.agency a
                ON a.id = cs.agency_id
            
            LEFT JOIN reconciliation.carrier ca
                ON ca.id = cs.carrier_id
            
            WHERE rc.status_id NOT IN (49, 50)
            
            AND NOT EXISTS (
                SELECT 1
                FROM reconciliation.commission_payment_detail cpd
                WHERE cpd.commission_statement_item_id = csi.id
            )
            
            AND (
                CAST(:producerName AS TEXT) IS NULL
                OR LOWER(pr.full_name) LIKE LOWER(
                    CONCAT('%', CAST(:producerName AS TEXT), '%')
                )
            )
            
            AND (
                CAST(:policyNumber AS TEXT) IS NULL
                OR LOWER(p.policy_number) LIKE LOWER(
                    CONCAT('%', CAST(:policyNumber AS TEXT), '%')
                )
            )
            
            AND (
                CAST(:agencyName AS TEXT) IS NULL
                OR LOWER(a.name) LIKE LOWER(
                    CONCAT('%', CAST(:agencyName AS TEXT), '%')
                )
            )
            
            AND (
                CAST(:carrierName AS TEXT) IS NULL
                OR LOWER(ca.name) LIKE LOWER(
                    CONCAT('%', CAST(:carrierName AS TEXT), '%')
                )
            )
            
            ORDER BY csi.created_at DESC
        """, nativeQuery = true)
    List<CommissionReconciliationView> findPendingCommissionReconciliations(
            @Param("producerName") String producerName,
            @Param("policyNumber") String policyNumber,
            @Param("agencyName") String agencyName,
            @Param("carrierName") String carrierName,
            Pageable pageable
    );

    @Query(value = """
        SELECT
            csi.id AS commissionStatementItemId,
            cs.id AS commissionStatementId,        
            p.id AS policyId,
            p.policy_number AS policyNumber,
            ps.id AS policyStatusId,
            ps.name AS policyStatusName,        
            cl.id AS clientId,
            cl.full_name AS clientName,        
            pr.id AS producerId,
            pr.full_name AS producerName,        
            a.id AS agencyId,
            a.name AS agencyName,        
            ca.id AS carrierId,
            ca.name AS carrierName,        
            csi.net_amount AS netAmount,
            csi.rate AS rate,
            csi.commission_rate_pct AS commissionRatePct,        
            ROUND((csi.net_amount * (csi.rate / 100) * (csi.commission_rate_pct / 100)), 2) AS estimatedPaymentAmount,        
            CASE
                WHEN csi.net_amount < 0 THEN 'NET_AMOUNT_NEGATIVE'
                ELSE 'INVALID_POLICY_STATUS'
            END AS reconciliationType,
        
            CASE
                WHEN csi.net_amount < 0 THEN 'Net Amount menor a cero'
                ELSE 'Estado de póliza no válido para pago'
            END AS reconciliationReason,
        
            csi.created_at AS createdAt
        
        FROM reconciliation.commission_statement_item csi
        JOIN reconciliation.commission_statement cs ON cs.id = csi.commission_statement_id
        JOIN reconciliation.policy p ON p.id = cs.policy_id
        JOIN reconciliation.parameter ps ON ps.id = p.status_id
        LEFT JOIN reconciliation.client cl ON cl.id = cs.client_id
        LEFT JOIN reconciliation.producer pr ON pr.id = cs.producer_id
        LEFT JOIN reconciliation.agency a ON a.id = cs.agency_id
        LEFT JOIN reconciliation.carrier ca ON ca.id = cs.carrier_id
        WHERE csi.id = :commissionStatementItemId
        """, nativeQuery = true)
    Optional<CommissionReconciliationView> findCommissionReconciliationByItemIdBefore(
            @Param("commissionStatementItemId") Long commissionStatementItemId
    );

    @Query(value = """
    SELECT
        csi.id AS commissionStatementItemId,
        cs.id AS commissionStatementId,
        p.id AS policyId,
        p.policy_number AS policyNumber,
        ps.id AS policyStatusId,
        ps.name AS policyStatusName,
        cl.id AS clientId,
        cl.full_name AS clientName,
        pr.id AS producerId,
        pr.full_name AS producerName,
        a.id AS agencyId,
        a.name AS agencyName,
        ca.id AS carrierId,
        ca.name AS carrierName,

        csi.net_amount AS netAmount,
        csi.rate AS rate,
        csi.commission_rate_pct AS commissionRatePct,

        ROUND(
            (
                csi.net_amount
                * (csi.rate / 100)
                * (csi.commission_rate_pct / 100)
            ),
            2
        ) AS estimatedPaymentAmount,

        STRING_AGG(DISTINCT case_type.name, ' | ') AS reconciliationType,

        STRING_AGG(DISTINCT rc.description, ' | ') AS reconciliationReason,

        csi.created_at AS createdAt

    FROM reconciliation.reconciliation_case rc

    JOIN reconciliation.commission_statement_item csi
        ON csi.id = rc.commission_statement_item_id

    JOIN reconciliation.commission_statement cs
        ON cs.id = rc.commission_statement_id

    JOIN reconciliation.policy p
        ON p.id = rc.policy_id

    JOIN reconciliation.parameter ps
        ON ps.id = p.status_id

    JOIN reconciliation.parameter case_type
        ON case_type.id = rc.case_type_id

    LEFT JOIN reconciliation.client cl
        ON cl.id = cs.client_id

    LEFT JOIN reconciliation.producer pr
        ON pr.id = cs.producer_id

    LEFT JOIN reconciliation.agency a
        ON a.id = cs.agency_id

    LEFT JOIN reconciliation.carrier ca
        ON ca.id = cs.carrier_id
    WHERE csi.id = :commissionStatementItemId
    GROUP BY
        csi.id,
        cs.id,
        p.id,
        p.policy_number,
        ps.id,
        ps.name,
        cl.id,
        cl.full_name,
        pr.id,
        pr.full_name,
        a.id,
        a.name,
        ca.id,
        ca.name,
        csi.net_amount,
        csi.rate,
        csi.commission_rate_pct,
        csi.created_at
    """, nativeQuery = true)
    Optional<CommissionReconciliationView> findCommissionReconciliationByItemId(@Param("commissionStatementItemId") Long commissionStatementItemId);
}
