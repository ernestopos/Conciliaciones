package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "policy", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;
    @Column(name = "policy_number", length = 150)
    private String policyNumber;
    @Column(name = "subscriber_id", length = 150)
    private String subscriberId;
    @Column(name = "effective_date")
    private LocalDate effectiveDate;
    @Column(name = "issue_date")
    private LocalDate issueDate;
    @Column(name = "termination_date")
    private LocalDate terminationDate;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private Parameter status;
    @Column(name = "resident_state", length = 100)
    private String residentState;
    @Column(name = "issue_state", length = 100)
    private String issueState;
    @Column(name = "members_count")
    private Integer membersCount;
    @Column(name = "source_key", length = 255)
    private String sourceKey;
    @Column(name = "active", nullable = false)
    private Boolean active;
}
