package com.conciliaciones.persistence.jpa.entity;

import com.conciliaciones.domain.audit.AuditInfo;

public interface Auditable {

    default AuditInfo toAuditInfo() {
        BaseAuditEntity entity = (BaseAuditEntity) this;
        return AuditInfo.builder()
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .modifiedBy(entity.getModifiedBy())
                .modifiedAt(entity.getModifiedAt())
                .build();
    }
}
