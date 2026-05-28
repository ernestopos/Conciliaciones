package com.conciliaciones.domain.processing;

import com.conciliaciones.domain.audit.AuditInfo;
import com.conciliaciones.domain.common.BaseDomainEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProcessingExecution extends BaseDomainEntity<Long> {

    private Long sourceFileId;
    private String executionId;
    private ProcessingStatus status;
    private String detailMessage;
    private AuditInfo auditInfo;
}
