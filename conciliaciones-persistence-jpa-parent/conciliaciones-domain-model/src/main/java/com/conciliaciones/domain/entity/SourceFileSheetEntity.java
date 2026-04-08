package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "source_file_sheet")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceFileSheetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="source_file_id",nullable=false)
    private Long sourceFileId;

    @Column(name="sheet_name",nullable=false,length=255)
    private String sheetName;

    @Column(name="sheet_order",nullable=false)
    private Integer sheetOrder;

    @Column(name="total_rows")
    private Integer totalRows;

    @Column(name="created_at",nullable=false)
    private java.time.LocalDateTime createdAt;

    @Column(name="created_by",length=100)
    private String createdBy;

}
