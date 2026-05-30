package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "policy")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="carrier_id",nullable=false)
    private Long carrierId;

    @Column(name="client_id")
    private Long clientId;

    @Column(name="policy_number",length=150)
    private String policyNumber;

    @Column(name="subscriber_id",length=150)
    private String subscriberId;

    @Column(name="effective_date")
    private java.time.LocalDate effectiveDate;

    @Column(name="issue_date")
    private java.time.LocalDate issueDate;

    @Column(name="termination_date")
    private java.time.LocalDate terminationDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private ParameterEntity statusId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resident_state", nullable = false)
    private ParameterEntity residentState;

    @Column(name="issue_state",length=100)
    private String issueState;

    @Column(name="members_count")
    private Integer membersCount;

    @Column(name="source_key",length=255)
    private String sourceKey;

    @Column(nullable=false)
    private Boolean active;

    @Column(name="created_at",nullable=false)
    private java.time.LocalDateTime createdAt;

    @Column(name="created_by",length=100)
    private String createdBy;

    @Column(name="updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name="updated_by",length=100)
    private String updatedBy;

}
