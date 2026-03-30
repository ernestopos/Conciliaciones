package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "reconciliation", name = "parameter")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 150)
    private String value;

    @Column(name = "parameter_group", nullable = false, length = 100)
    private String parameterGroup;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

}
