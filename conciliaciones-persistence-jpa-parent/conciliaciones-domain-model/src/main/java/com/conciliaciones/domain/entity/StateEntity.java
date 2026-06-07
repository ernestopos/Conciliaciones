package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        schema = "reconciliation",
        name = "state",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_state_country_code", columnNames = {"country_id", "code"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity country;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "active", nullable = false)
    private Boolean active;
}