package com.conciliaciones.persistence.repository;

import com.conciliaciones.persistence.jpa.entity.SourceFileValidationEntity;
import com.conciliaciones.persistence.projection.ValidationExecutionDetailView;
import com.conciliaciones.persistence.repository.projection.SourceFileValidationDetailProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SourceFileValidationRepository extends JpaRepository<SourceFileValidationEntity, Long> {

    @Query("""
            SELECT validation
              FROM SourceFileValidationEntity validation
              JOIN validation.validationSourcePlan plan
             WHERE plan.sourceFile.id = :sourceFileId
             ORDER BY validation.createdAt ASC
            """)
    List<SourceFileValidationEntity> findBySourceFileIdOrderByCreatedAtAsc(@Param("sourceFileId") Long sourceFileId);

    @Query("""
            SELECT validation
              FROM SourceFileValidationEntity validation
              JOIN validation.validationSourcePlan plan
             WHERE plan.sourceFile.id = :sourceFileId
               AND validation.validationStatusId = :validationStatusId
             ORDER BY validation.createdAt ASC
            """)
    List<SourceFileValidationEntity> findBySourceFileIdAndValidationStatusIdOrderByCreatedAtAsc(
            @Param("sourceFileId") Long sourceFileId,
            @Param("validationStatusId") Long validationStatusId
    );

    @Query(value = """
            SELECT
                vsp.started_at AS startedAt,
                vsp.finished_at AS finishedAt,
                vsp.successful AS successful,
                vsp.message AS message,
                vt.description AS validationDescription,
                vs.description AS statusDescription
            FROM reconciliation.source_file_validation sfv
            INNER JOIN reconciliation.validation_source_plan vsp
                    ON sfv.validation_source_plan_id = vsp.id
            INNER JOIN reconciliation.parameter vt
                    ON vt.id = sfv.validation_type_id
            INNER JOIN reconciliation.parameter vs
                    ON vs.id = sfv.validation_status_id
            WHERE vsp.source_file_id = :sourceFileId
            ORDER BY sfv.id
            """, nativeQuery = true)
    List<ValidationExecutionDetailView> findValidationExecutionDetailBySourceFileId(@Param("sourceFileId") Long sourceFileId);

    @Query("""
    SELECT
        vsp.startedAt AS startedAt,
        vsp.finishedAt AS finishedAt,
        vsp.successful AS successful,
        sfv.message AS message,
        sfv.validationTypeId.description AS validationTypeDescription,
        vs.description AS validationStatusDescription
    FROM SourceFileValidationEntity sfv
    JOIN sfv.validationSourcePlan vsp
    JOIN ParameterEntity vs ON vs.id = sfv.validationStatusId
    WHERE vsp.sourceFile.id = (
        SELECT ept.sourceFile.id
        FROM ExecutionPlanTaskEntity ept
        WHERE ept.id = :executionPlanTaskId
    )
    ORDER BY sfv.id ASC
""")
    List<SourceFileValidationDetailProjection> findValidationDetailByExecutionPlanTaskId(@Param("executionPlanTaskId") Long executionPlanTaskId);

}
