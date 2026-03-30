package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "producer")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProducerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="agency_id")
    private Long agencyId;

    @Column(name="external_producer_id",length=100)
    private String externalProducerId;

    @Column(name="first_name",length=100)
    private String firstName;

    @Column(name="last_name",length=100)
    private String lastName;

    @Column(name="full_name",nullable=false,length=250)
    private String fullName;

    @Column(length=150)
    private String email;

    @Column(length=50)
    private String phone;

    @Column(length=100)
    private String npn;

    @Column(name="tax_id_masked",length=50)
    private String taxIdMasked;

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
