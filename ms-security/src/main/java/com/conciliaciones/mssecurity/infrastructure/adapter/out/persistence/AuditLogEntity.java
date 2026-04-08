package com.conciliaciones.mssecurity.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "security_audit_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario", nullable = false, length = 120)
    private String usuario;

    @Column(name = "accion", nullable = false, length = 120)
    private String accion;

    @Column(name = "fecha", nullable = false)
    private OffsetDateTime fecha;

    @Column(name = "resultado", nullable = false, length = 30)
    private String resultado;

    @Column(name = "detalle", length = 500)
    private String detalle;

    @Column(name = "estado", length = 40)
    private String estado;

    @Column(name = "valor_antes", columnDefinition = "jsonb")
    private String valorAntes;

    @Column(name = "valor_despues", columnDefinition = "jsonb")
    private String valorDespues;
}
