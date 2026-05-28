package com.conciliaciones.msmanagementtask.infrastructure.scheduler.validation;

import com.conciliaciones.domain.entity.RawImportRecordEntity;

public record ValidationError(
        RawImportRecordEntity rawImportRecord,
        Integer rowNumber,
        String columnName,
        String fieldValue,
        String message,
        String technicalDetail
) {
    public static ValidationError general(String message, String technicalDetail) {
        return new ValidationError(null, null, null, null, message, technicalDetail);
    }

    public static ValidationError row(RawImportRecordEntity raw, String columnName, String fieldValue, String message) {
        return new ValidationError(raw, raw.getRowNumber(), columnName, fieldValue, message, null);
    }
}
