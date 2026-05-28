package com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation;

import com.conciliaciones.domain.entity.RawImportRecordEntity;
import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import com.conciliaciones.persistence.jpa.entity.ValidationSourcePlanEntity;
import java.util.List;

public record ValidationTaskContext(
        SourceFileEntity sourceFile,
        ValidationSourcePlanEntity validationPlan,
        List<RawImportRecordEntity> rawRecords
) {
}
