package com.conciliaciones.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table( schema = "reconciliation", name = "carrier_portal")
public class CarrierPortalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /*
     * La relación es opcional porque carrier_id permite NULL
     * en el script de base de datos.
     *
     * Ajusta CarrierEntity por el nombre real de la entidad
     * carrier existente en el proyecto.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "carrier_id", referencedColumnName = "id",foreignKey = @jakarta.persistence.ForeignKey(name = "fk_carrier_portal_carrier"))
    private CarrierEntity carrier;

    @Column(name = "code",nullable = false,unique = true,length = 50)
    private String code;

    @Column(name = "display_name",nullable = false,length = 150)
    private String displayName;

    @Column(name = "portal_url",nullable = false,length = 1000)
    private String portalUrl;

    @Column(name = "logo_url", length = 1000)
    private String logoUrl;

    @Column(name = "description",length = 500)
    private String description;

    @Column(name = "active",nullable = false)
    private Boolean active = Boolean.TRUE;

    @Column(name = "sort_order",nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "allow_upload",nullable = false)
    private Boolean allowUpload = Boolean.TRUE;

    @Column(name = "allow_download",nullable = false)
    private Boolean allowDownload = Boolean.TRUE;

    @Column(name = "requires_mfa",nullable = false)
    private Boolean requiresMfa = Boolean.FALSE;

    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by",nullable = false,updatable = false,length = 100)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by",length = 100)
    private String updatedBy;

    @OrderBy("id ASC")
    @OneToMany(mappedBy = "carrierPortal",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CarrierPortalDomainEntity> domains = new ArrayList<>();

    @PrePersist
    protected void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (active == null) {
            active = Boolean.TRUE;
        }

        if (sortOrder == null) {
            sortOrder = 0;
        }

        if (allowUpload == null) {
            allowUpload = Boolean.TRUE;
        }

        if (allowDownload == null) {
            allowDownload = Boolean.TRUE;
        }

        if (requiresMfa == null) {
            requiresMfa = Boolean.FALSE;
        }
    }

    @PreUpdate
    protected void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Agrega un dominio y mantiene sincronizada la relación bidireccional.
     */
    public void addDomain(CarrierPortalDomainEntity domain) {
        if (domain == null) {
            return;
        }
        domains.add(domain);
        domain.setCarrierPortal(this);
    }

    /**
     * Elimina un dominio y rompe correctamente la relación.
     */
    public void removeDomain(CarrierPortalDomainEntity domain) {
        if (domain == null) {
            return;
        }
        domains.remove(domain);
        domain.setCarrierPortal(null);
    }
}