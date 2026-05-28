package com.conciliaciones.domain.audit;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditInfo {

    private String createdBy;
    private OffsetDateTime createdAt;
    private String modifiedBy;
    private OffsetDateTime modifiedAt;
}
