package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "agency")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="carrier_id")
    private Long carrierId;

    @Column(name="external_agency_id",length=100)
    private String externalAgencyId;

    @Column(nullable=false,length=200)
    private String name;

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
