package com.conciliaciones.persistence.mapper;

import com.conciliaciones.domain.processing.ProcessingExecution;
import com.conciliaciones.persistence.jpa.entity.ProcessingExecutionEntity;
import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProcessingExecutionPersistenceMapper {

    @Mapping(target = "sourceFileId", source = "sourceFile.id")
    @Mapping(target = "auditInfo", expression = "java(entity.toAuditInfo())")
    ProcessingExecution toDomain(ProcessingExecutionEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "sourceFile", expression = "java(toSourceFileEntity(execution.getSourceFileId()))")
    @Mapping(target = "createdBy", source = "auditInfo.createdBy")
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "modifiedBy", source = "auditInfo.modifiedBy")
    @Mapping(target = "modifiedAt", source = "auditInfo.modifiedAt")
    ProcessingExecutionEntity toEntity(ProcessingExecution execution);

    default SourceFileEntity toSourceFileEntity(Long id) {
        if (id == null) {
            return null;
        }
        SourceFileEntity entity = new SourceFileEntity();
        entity.setId(id);
        return entity;
    }
}
