package com.conciliaciones.persistence.mapper;

import com.conciliaciones.domain.file.SourceFile;
import com.conciliaciones.persistence.jpa.entity.SourceFileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SourceFilePersistenceMapper {

    @Mapping(target = "auditInfo", expression = "java(entity.toAuditInfo())")
    SourceFile toDomain(SourceFileEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdBy", source = "auditInfo.createdBy")
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "modifiedBy", source = "auditInfo.modifiedBy")
    @Mapping(target = "modifiedAt", source = "auditInfo.modifiedAt")
    SourceFileEntity toEntity(SourceFile sourceFile);
}
