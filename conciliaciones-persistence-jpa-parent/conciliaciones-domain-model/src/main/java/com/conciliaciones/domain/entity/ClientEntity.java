package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "client")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="external_client_id",length=100)
    private String externalClientId;

    @Column(name="first_name",length=100)
    private String firstName;

    @Column(name="middle_name",length=100)
    private String middleName;

    @Column(name="last_name",length=100)
    private String lastName;

    @Column(name="full_name",nullable=false,length=250)
    private String fullName;

    @Column(name="birth_date")
    private java.time.LocalDate birthDate;

    @Column(length=100)
    private String state;

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
