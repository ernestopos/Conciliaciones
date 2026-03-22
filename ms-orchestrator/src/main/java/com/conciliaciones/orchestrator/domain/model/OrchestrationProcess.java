package com.conciliaciones.orchestrator.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrchestrationProcess {
    private Long id;
    private String processCode;
    private String businessCode;
    private ProcessStatus status;
    private LocalDateTime createdAt;
}
