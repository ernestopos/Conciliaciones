package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "client", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "external_client_id", length = 100)
    private String externalClientId;
    @Column(name = "first_name", length = 100)
    private String firstName;
    @Column(name = "middle_name", length = 100)
    private String middleName;
    @Column(name = "last_name", length = 100)
    private String lastName;
    @Column(name = "full_name", nullable = false, length = 250)
    private String fullName;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "state", length = 100)
    private String state;
    @Column(name = "active", nullable = false)
    private Boolean active;
}
