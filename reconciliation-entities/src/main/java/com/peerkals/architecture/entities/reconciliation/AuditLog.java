package com.peerkals.architecture.entities.reconciliation;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "entity_name", nullable = false, length = 150)
    private String entityName;
    @Column(name = "entity_id", nullable = false, length = 100)
    private String entityId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "action_id", nullable = false)
    private Parameter action;
    @Column(name = "username", length = 150)
    private String username;
    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;
    @Column(name = "old_values", columnDefinition = "jsonb")
    private String oldValues;
    @Column(name = "new_values", columnDefinition = "jsonb")
    private String newValues;
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
}
