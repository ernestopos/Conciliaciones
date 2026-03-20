package com.peerkals.architecture.entities.reconciliation;

import com.peerkals.architecture.entities.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parameter", schema = "reconciliation",
       uniqueConstraints = {
           @UniqueConstraint(name = "uq_parameter_group_name", columnNames = {"parameter_group", "name"}),
           @UniqueConstraint(name = "uq_parameter_group_value", columnNames = {"parameter_group", "value"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parameter extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    @Column(name = "description", length = 500)
    private String description;
    @Column(name = "value", nullable = false, length = 150)
    private String value;
    @Column(name = "parameter_group", nullable = false, length = 100)
    private String parameterGroup;
    @Column(name = "active", nullable = false)
    private Boolean active;
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
