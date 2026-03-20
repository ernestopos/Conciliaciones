package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carrier", schema = "reconciliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrier extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;
    @Column(name = "name", nullable = false, length = 150, unique = true)
    private String name;
    @Column(name = "description", length = 500)
    private String description;
    @Column(name = "active", nullable = false)
    private Boolean active;
}
