package com.conciliaciones.persistence.jpa.entity;

import com.conciliaciones.domain.entity.ParameterEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "execution_plan_task", schema = "reconciliation")
public class ExecutionPlanTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_source_file", nullable = false)
    private SourceFileEntity sourceFile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_status", nullable = false)
    private ParameterEntity status;

    @Column(name = "plan_execute_code", nullable = false, length = 200)
    private String planExecuteCode;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "successful")
    private Boolean successful;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @OneToMany(mappedBy = "executionPlanTask", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<ScheduledTaskEntity> scheduledTasks = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
    }
}
