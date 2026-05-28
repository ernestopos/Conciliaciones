package com.conciliaciones.reconciliation.core.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Reconciliation {
    private Long id;
    private String businessCode;
    private String sourceFileName;
    private ReconciliationStatus status;
    private LocalDateTime createdAt;
}
