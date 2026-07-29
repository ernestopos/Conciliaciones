package com.conciliaciones.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(schema = "reconciliation", name = "carrier_portal_domain")
public class CarrierPortalDomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "carrier_portal_id",nullable = false,referencedColumnName = "id",
                foreignKey = @jakarta.persistence.ForeignKey(name = "fk_carrier_portal_domain"))
    private CarrierPortalEntity carrierPortal;

    @Column(name = "domain_pattern",nullable = false,length = 500)
    private String domainPattern;

    @Column(name = "domain_type",nullable = false,length = 30)
    private String domainType = "PRIMARY";

    @Column(name = "active",nullable = false)
    private Boolean active = Boolean.TRUE;

    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (domainType == null || domainType.isBlank()) {
            domainType = "PRIMARY";
        }
        if (active == null) {
            active = Boolean.TRUE;
        }
    }
}