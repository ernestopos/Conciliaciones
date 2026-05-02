package com.conciliaciones.persistence.jpa.entity;

import com.conciliaciones.domain.task.ScheduledTaskStatus;
import com.conciliaciones.domain.task.TaskType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scheduled_task", schema = "reconciliation")
public class ScheduledTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 80)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private ScheduledTaskStatus status;

    @Column(name = "source_file_id")
    private Long sourceFileId;

    @Column(name = "cron_expression", nullable = false, length = 80)
    private String cronExpression;

    @Column(name = "task_bean_name", nullable = false, length = 150)
    private String taskBeanName;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_by", nullable = false, length = 80)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by", length = 80)
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (active == null) active = Boolean.TRUE;
        if (status == null) status = ScheduledTaskStatus.PENDING;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
