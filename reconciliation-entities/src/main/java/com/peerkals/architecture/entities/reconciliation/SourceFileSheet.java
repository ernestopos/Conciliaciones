package com.peerkals.architecture.entities.reconciliation;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "source_file_sheet", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceFileSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_file_id", nullable = false)
    private SourceFile sourceFile;
    @Column(name = "sheet_name", nullable = false, length = 255)
    private String sheetName;
    @Column(name = "sheet_order", nullable = false)
    private Integer sheetOrder;
    @Column(name = "total_rows")
    private Integer totalRows;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "created_by", length = 100)
    private String createdBy;
}
