package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CommissionPaymentEntity;
import com.conciliaciones.persistence.projection.CommissionReportRow;
import com.conciliaciones.persistence.projection.PaymentMonthlyForCarrierReportRow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommissionPaymentRepository extends JpaRepository<CommissionPaymentEntity, Long> {

    @Query("""
            SELECT new com.conciliaciones.persistence.projection.CommissionReportRow(
                pr.fullName,
                p.policyNumber,
                csi.netAmount,
                csi.rate,
                csi.commissionRatePct,
                csi.commissionAmount,
                cpd.amount
            )
            FROM CommissionPaymentDetailEntity cpd
            JOIN cpd.policyId p
            JOIN cpd.commissionStatementItemId csi
            JOIN CommissionStatementEntity cms ON cms.id = csi.commissionStatementId
            LEFT JOIN ProducerEntity pr ON pr.id = cms.producerId
            ORDER BY cpd.id DESC
            """)
    List<CommissionReportRow> findCommissionReport(Pageable pageable);

    @Query(value = """
            SELECT
                ca.name AS "carrierName",
                ag.external_agency_id::text AS "agencyId",
                ag.name AS agency,
                p.external_producer_id::text AS "producerId",
                p.full_name AS producer,
                COALESCE(SUM(CASE WHEN EXTRACT(MONTH FROM cs.paid_date)::int = 1 THEN cpd.amount ELSE 0 END), 0) AS january,
                COALESCE(SUM(CASE WHEN EXTRACT(MONTH FROM cs.paid_date)::int = 2 THEN cpd.amount ELSE 0 END), 0) AS february,
                COALESCE(SUM(CASE WHEN EXTRACT(MONTH FROM cs.paid_date)::int = 3 THEN cpd.amount ELSE 0 END), 0) AS march,
                COALESCE(SUM(CASE WHEN EXTRACT(MONTH FROM cs.paid_date)::int = 4 THEN cpd.amount ELSE 0 END), 0) AS april,
                COALESCE(SUM(CASE WHEN EXTRACT(MONTH FROM cs.paid_date)::int = 5 THEN cpd.amount ELSE 0 END), 0) AS may,
                COALESCE(SUM(CASE WHEN EXTRACT(MONTH FROM cs.paid_date)::int = 6 THEN cpd.amount ELSE 0 END), 0) AS june,
                COALESCE(SUM(CASE WHEN EXTRACT(MONTH FROM cs.paid_date)::int = 7 THEN cpd.amount ELSE 0 END), 0) AS july,
                COALESCE(SUM(CASE WHEN EXTRACT(MONTH FROM cs.paid_date)::int = 8 THEN cpd.amount ELSE 0 END), 0) AS august,
                COALESCE(SUM(CASE WHEN EXTRACT(MONTH FROM cs.paid_date)::int = 9 THEN cpd.amount ELSE 0 END), 0) AS september,
                COALESCE(SUM(CASE WHEN EXTRACT(MONTH FROM cs.paid_date)::int = 10 THEN cpd.amount ELSE 0 END), 0) AS october,
                COALESCE(SUM(CASE WHEN EXTRACT(MONTH FROM cs.paid_date)::int = 11 THEN cpd.amount ELSE 0 END), 0) AS november,
                COALESCE(SUM(CASE WHEN EXTRACT(MONTH FROM cs.paid_date)::int = 12 THEN cpd.amount ELSE 0 END), 0) AS december,
                COALESCE(SUM(cpd.amount), 0) AS total
            FROM reconciliation.commission_statement cs
            INNER JOIN reconciliation.carrier ca ON ca.id = cs.carrier_id
            INNER JOIN reconciliation.agency ag ON ag.id = cs.agency_id
            INNER JOIN reconciliation.producer p ON p.id = cs.producer_id
            INNER JOIN reconciliation.commission_payment_detail cpd ON cpd.policy_id = cs.policy_id
            WHERE EXTRACT(YEAR FROM cs.paid_date)::int = :reportYear
            GROUP BY
                ca.name,
                ag.external_agency_id,
                ag.name,
                p.external_producer_id,
                p.full_name
            ORDER BY
                ca.name,
                ag.name,
                p.full_name
            """, nativeQuery = true)
    List<PaymentMonthlyForCarrierReportRow> findPaymentMonthlyForCarrierReport(@Param("reportYear") Integer reportYear);
}