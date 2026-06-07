import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { GenericTableComponent, TableAction } from '../../../shared/components/generic-table/generic-table.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SearchFilterBarComponent } from '../../../shared/components/search-filter-bar/search-filter-bar.component';
import { Carrier } from '../../../models/carrier.model';
import { Parameter } from '../../../models/parameter.model';
import { Policy } from '../../../models/policy.model';
import { CarrierService } from '../../../services/carrier.service';
import { ParameterService } from '../../../services/parameter.service';
import { PolicyService } from '../../../services/policy.service';
import { PolicyFormDialogComponent } from './policy-form-dialog.component';

@Component({
  selector: 'app-policies-page',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatDialogModule,
    MatIconModule,
    MatSnackBarModule,
    ConfirmDialogComponent,
    EmptyStateComponent,
    GenericTableComponent,
    LoadingSpinnerComponent,
    PageHeaderComponent,
    SearchFilterBarComponent
  ],
  template: `
    <div class="page-shell">
      <div class="page-header-row">
        <app-page-header title="Pólizas" subtitle="Administración del catálogo maestro de pólizas." />
        <button mat-raised-button color="primary" type="button" (click)="openCreate()">
          <mat-icon>add</mat-icon>
          Nueva póliza
        </button>
      </div>

      <app-search-filter-bar (filterChange)="applyFilter($event)" />

      @if (loading) {
        <app-loading-spinner />
      } @else if (!filteredPolicies.length) {
        <app-empty-state title="Sin pólizas" description="No hay pólizas disponibles para los filtros aplicados." />
      } @else {
        <app-generic-table [data]="filteredPolicies" [displayedColumns]="columns" [headers]="headers" [actions]="tableActions" />
      }
    </div>
  `,
  styles: [
    `.page-shell{display:block}`,
    `.page-header-row{display:flex;justify-content:space-between;align-items:flex-end;gap:16px;flex-wrap:wrap;margin-bottom:8px}`
  ]
})
export class PoliciesPageComponent implements OnInit {
  readonly columns = ['id', 'carrierName', 'clientName', 'policyNumber', 'subscriberId', 'effectiveDate', 'statusName', 'residentCountryName', 'residentStateName', 'residentCityName', 'active'];
  readonly headers: Record<string, string> = {
    id: 'ID',
    carrierName: 'Carrier',
    clientName: 'Cliente',
    policyNumber: 'Póliza',
    subscriberId: 'Subscriber ID',
    effectiveDate: 'Fecha efectiva',
    statusName: 'Estado póliza',
    residentCountryName: 'País',
    residentStateName: 'Estado',
    residentCityName: 'Ciudad',
    active: 'Activo'
  };

  policies: Policy[] = [];
  filteredPolicies: Policy[] = [];
  carriers: Carrier[] = [];
  parameters: Parameter[] = [];
  loading = true;

  readonly tableActions: TableAction<Policy>[] = [
    { label: 'Editar', icon: 'edit', callback: (row) => this.openEdit(row) },
    { label: 'Eliminar', icon: 'delete', color: 'warn', callback: (row) => this.confirmDelete(row) }
  ];

  constructor(
    private readonly policyService: PolicyService,
    private readonly carrierService: CarrierService,
    private readonly parameterService: ParameterService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.load();
  }

  applyFilter(query: string): void {
    const normalized = query.trim().toLowerCase();
    if (!normalized) {
      this.filteredPolicies = [...this.policies];
      return;
    }

    this.filteredPolicies = this.policies.filter((item) => JSON.stringify(item).toLowerCase().includes(normalized));
  }

  openCreate(): void {
    const dialogRef = this.dialog.open(PolicyFormDialogComponent, {
      width: '980px',
      maxWidth: '96vw',
      data: {
        title: 'Nueva póliza',
        carriers: this.carriers,
        parameters: this.parameters
      }
    });

    dialogRef.afterClosed().subscribe((payload?: Partial<Policy>) => {
      if (!payload) {
        return;
      }

      this.policyService.create(payload).subscribe({
        next: () => {
          this.snackBar.open('Póliza creada correctamente.', 'Cerrar', { duration: 2400 });
          this.load();
        },
        error: () => this.snackBar.open('No fue posible crear la póliza.', 'Cerrar', { duration: 3200 })
      });
    });
  }

  private openEdit(policy: Policy): void {
    const dialogRef = this.dialog.open(PolicyFormDialogComponent, {
      width: '980px',
      maxWidth: '96vw',
      data: {
        title: 'Editar póliza',
        policy,
        carriers: this.carriers,
        parameters: this.parameters
      }
    });

    dialogRef.afterClosed().subscribe((payload?: Partial<Policy>) => {
      if (!payload || policy.id == null) {
        return;
      }

      this.policyService.update(policy.id, payload).subscribe({
        next: () => {
          this.snackBar.open('Póliza actualizada correctamente.', 'Cerrar', { duration: 2400 });
          this.load();
        },
        error: () => this.snackBar.open('No fue posible actualizar la póliza.', 'Cerrar', { duration: 3200 })
      });
    });
  }

  private confirmDelete(policy: Policy): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Eliminar póliza',
        message: `¿Confirma la eliminación de la póliza ${policy.policyNumber ?? policy.id}?`
      }
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed || policy.id == null) {
        return;
      }

      this.policyService.delete(policy.id).subscribe({
        next: () => {
          this.snackBar.open('Póliza eliminada correctamente.', 'Cerrar', { duration: 2400 });
          this.load();
        },
        error: () => this.snackBar.open('No fue posible eliminar la póliza.', 'Cerrar', { duration: 3200 })
      });
    });
  }

  private load(): void {
    this.loading = true;

    forkJoin({
      policies: this.policyService.list(),
      carriers: this.carrierService.list(),
      parameters: this.parameterService.list()
    }).subscribe({
      next: ({ policies, carriers, parameters }) => {
        this.carriers = carriers;
        this.parameters = parameters;
        this.policies = policies.map((policy) => this.enrichPolicy(policy));
        this.filteredPolicies = [...this.policies];
        this.loading = false;
      },
      error: () => {
        this.policies = [];
        this.filteredPolicies = [];
        this.loading = false;
        this.snackBar.open('No fue posible consultar las pólizas.', 'Cerrar', { duration: 3200 });
      }
    });
  }

  private enrichPolicy(policy: Policy): Policy {
    const carrier = this.carriers.find((item) => item.id === policy.carrierId);
    const status = this.parameters.find((item) => item.id === policy.statusId);
    return {
      ...policy,
      carrierName: carrier?.name ?? String(policy.carrierId ?? '—'),
      clientName: policy.clientName ?? (policy.clientId ? `Cliente ID ${policy.clientId}` : '—'),
      statusName: policy.statusName ?? status?.name ?? String(policy.statusId ?? '—'),
      residentCountryName: policy.residentCountryName ?? '—',
      residentStateName: policy.residentStateName ?? '—',
      residentCityName: policy.residentCityName ?? String(policy.residentCityId ?? '—')
    };
  }
}
