package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "security_sub_menu",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_security_sub_menu_code", columnNames = "code"),
                @UniqueConstraint(name = "uk_security_sub_menu_parameter", columnNames = "parameter_id"),
                @UniqueConstraint(name = "uk_security_sub_menu_route", columnNames = "route")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecuritySubMenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    private SecurityMenuEntity menu;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parameter_id", nullable = false)
    private ParameterEntity parameter;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "label", nullable = false, length = 150)
    private String label;

    @Column(name = "route", nullable = false, unique = true, length = 200)
    private String route;

    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}