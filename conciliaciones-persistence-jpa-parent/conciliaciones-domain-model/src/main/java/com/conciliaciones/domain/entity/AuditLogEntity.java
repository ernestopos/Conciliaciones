package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "audit_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="entity_name",nullable=false,length=150)
    private String entityName;

    @Column(name="entity_id",nullable=false,length=100)
    private String entityId;

    @Column(name="action_id",nullable=false)
    private Long actionId;

    @Column(length=150)
    private String username;

    @Column(name="event_timestamp",nullable=false)
    private java.time.LocalDateTime eventTimestamp;

    @Column(name="old_values",columnDefinition="jsonb")
    private String oldValues;

    @Column(name="new_values",columnDefinition="jsonb")
    private String newValues;

    @Column(columnDefinition="TEXT")
    private String details;

}
