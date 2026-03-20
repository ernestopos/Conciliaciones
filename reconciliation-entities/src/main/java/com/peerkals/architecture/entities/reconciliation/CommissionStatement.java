package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "commission_statement", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionStatement extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_file_id", nullable = false)
    private SourceFile sourceFile;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_import_record_id")
    private RawImportRecord rawImportRecord;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producer_id")
    private Producer producer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private Policy policy;
    @Column(name = "statement_date")
    private LocalDate statementDate;
    @Column(name = "check_date")
    private LocalDate checkDate;
    @Column(name = "paid_date")
    private LocalDate paidDate;
    @Column(name = "last_pay_date")
    private LocalDate lastPayDate;
    @Column(name = "transaction_date")
    private LocalDate transactionDate;
    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;
    @Column(name = "row_identifier", length = 255)
    private String rowIdentifier;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_status_id")
    private Parameter rawStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_code_id")
    private Parameter reasonCode;
    @Column(name = "source_sheet_name", length = 255)
    private String sourceSheetName;
    @Column(name = "source_row_number")
    private Integer sourceRowNumber;
}
