package com.conciliaciones.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        schema = "reconciliation",
        name = "city",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_city_state_name", columnNames = {"state_id", "name"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private StateEntity state;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "active", nullable = false)
    private Boolean active;
}