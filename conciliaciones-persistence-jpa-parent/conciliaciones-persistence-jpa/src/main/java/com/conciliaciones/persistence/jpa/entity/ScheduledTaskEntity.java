package com.conciliaciones.persistence.jpa.entity;

import com.conciliaciones.domain.entity.ParameterEntity;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_task_type", nullable = false)
    private ParameterEntity taskType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_status", nullable = false)
    private ParameterEntity status;

    @Column(name = "cron_expression", nullable = false, length = 80)
    private String cronExpression;

    @Column(name = "task_bean_name", nullable = false, length = 200)
    private String taskBeanName;

    @Column(name = "method_name", nullable = false, length = 200)
    private String methodName;

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
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (active == null) {
            active = Boolean.TRUE;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}