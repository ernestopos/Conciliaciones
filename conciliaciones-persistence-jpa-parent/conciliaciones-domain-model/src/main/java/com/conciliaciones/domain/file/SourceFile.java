package com.conciliaciones.domain.file;

import com.conciliaciones.domain.audit.AuditInfo;
import com.conciliaciones.domain.common.BaseDomainEntity;
import com.conciliaciones.domain.processing.ProcessingStatus;
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
public class SourceFile extends BaseDomainEntity<Long> {

    private Long clientId;
    private String originalFileName;
    private String storagePath;
    private String mimeType;
    private Long fileSize;
    private FileType fileType;
    private String checksum;
    private ProcessingStatus processingStatus;
    private AuditInfo auditInfo;
}
