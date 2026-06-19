package com.conciliaciones.persistence.repository;

import com.conciliaciones.domain.entity.CommissionPaymentEntity;
import com.conciliaciones.persistence.projection.CommissionReportRow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}