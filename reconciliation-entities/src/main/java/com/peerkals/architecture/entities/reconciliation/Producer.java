package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "producer", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producer extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;
    @Column(name = "external_producer_id", length = 100, unique = true)
    private String externalProducerId;
    @Column(name = "first_name", length = 100)
    private String firstName;
    @Column(name = "last_name", length = 100)
    private String lastName;
    @Column(name = "full_name", nullable = false, length = 250)
    private String fullName;
    @Column(name = "email", length = 150)
    private String email;
    @Column(name = "phone", length = 50)
    private String phone;
    @Column(name = "npn", length = 100)
    private String npn;
    @Column(name = "tax_id_masked", length = 50)
    private String taxIdMasked;
    @Column(name = "active", nullable = false)
    private Boolean active;
}
