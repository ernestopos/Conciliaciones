package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "commission_statement")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionStatementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="source_file_id",nullable=false)
    private Long sourceFileId;

    @Column(name="raw_import_record_id")
    private Long rawImportRecordId;

    @Column(name="carrier_id",nullable=false)
    private Long carrierId;

    @Column(name="agency_id")
    private Long agencyId;

    @Column(name="producer_id")
    private Long producerId;

    @Column(name="client_id")
    private Long clientId;

    @Column(name="policy_id")
    private Long policyId;

    @Column(name="statement_date")
    private java.time.LocalDate statementDate;

    @Column(name="check_date")
    private java.time.LocalDate checkDate;

    @Column(name="paid_date")
    private java.time.LocalDate paidDate;

    @Column(name="last_pay_date")
    private java.time.LocalDate lastPayDate;

    @Column(name="transaction_date")
    private java.time.LocalDate transactionDate;

    @Column(name="invoice_number",length=100)
    private String invoiceNumber;

    @Column(name="row_identifier",length=255)
    private String rowIdentifier;

    @Column(name="raw_status_id")
    private Long rawStatusId;

    @Column(name="reason_code_id")
    private Long reasonCodeId;

    @Column(name="source_sheet_name",length=255)
    private String sourceSheetName;

    @Column(name="source_row_number")
    private Integer sourceRowNumber;

    @Column(name="created_at",nullable=false)
    private java.time.LocalDateTime createdAt;

    @Column(name="created_by",length=100)
    private String createdBy;

    @Column(name="updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name="updated_by",length=100)
    private String updatedBy;

}
