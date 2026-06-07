import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Carrier } from '../../../models/carrier.model';
import { Client } from '../../../models/client.model';
import { Parameter } from '../../../models/parameter.model';
import { Policy } from '../../../models/policy.model';
import { CarrierService } from '../../../services/carrier.service';
import { ParameterService } from '../../../services/parameter.service';
import { City } from '../../../models/location.model';
import { ClientSearchDialogComponent } from './client-search-dialog.component';
import { CitySearchDialogComponent } from './city-search-dialog.component';

interface PolicyFormDialogData {
  title: string;
  policy?: Policy;
  carriers: Carrier[];
  parameters: Parameter[];
}

@Component({
  selector: 'app-policy-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>

    <mat-dialog-content>
      <form [formGroup]="form" class="policy-form">
        <mat-form-field appearance="outline">
          <mat-label>Carrier</mat-label>
          <mat-select formControlName="carrierId">
            <mat-option *ngFor="let carrier of carriers" [value]="carrier.id">{{ carrier.name }}</mat-option>
          </mat-select>
          <mat-error>Campo requerido</mat-error>
        </mat-form-field>

        <div class="client-field">
          <mat-form-field appearance="outline">
            <mat-label>Cliente</mat-label>
            <input matInput [value]="selectedClientLabel" readonly />
          </mat-form-field>
          <button mat-stroked-button color="primary" type="button" (click)="openClientSearch()">
            <mat-icon>person_search</mat-icon>
            Buscar
          </button>
        </div>

        <mat-form-field appearance="outline">
          <mat-label>Número de póliza</mat-label>
          <input matInput formControlName="policyNumber" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Subscriber ID</mat-label>
          <input matInput formControlName="subscriberId" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Fecha efectiva</mat-label>
          <input matInput type="date" formControlName="effectiveDate" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Fecha emisión</mat-label>
          <input matInput type="date" formControlName="issueDate" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Fecha terminación</mat-label>
          <input matInput type="date" formControlName="terminationDate" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Estado póliza</mat-label>
          <mat-select formControlName="statusId">
            <mat-option *ngFor="let status of statusOptions" [value]="status.id">{{ status.name }}</mat-option>
          </mat-select>
          <mat-error>Campo requerido</mat-error>
        </mat-form-field>

        <div class="location-field">
          <mat-form-field appearance="outline">
            <mat-label>Residencia</mat-label>
            <input matInput [value]="selectedCityLabel" readonly />
            <mat-error>Campo requerido</mat-error>
          </mat-form-field>
          <button mat-stroked-button color="primary" type="button" (click)="openCitySearch()">
            <mat-icon>travel_explore</mat-icon>
            Buscar
          </button>
        </div>

        <mat-form-field appearance="outline">
          <mat-label>Issue State</mat-label>
          <input matInput formControlName="issueState" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Cantidad miembros</mat-label>
          <input matInput type="number" formControlName="membersCount" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Source Key</mat-label>
          <input matInput formControlName="sourceKey" />
        </mat-form-field>

        <mat-checkbox formControlName="active">Activo</mat-checkbox>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button type="button" (click)="dialogRef.close()">Cancelar</button>
      <button mat-raised-button color="primary" type="button" (click)="submit()">Guardar</button>
    </mat-dialog-actions>
  `,
  styles: [
    `.policy-form{display:grid;grid-template-columns:repeat(auto-fit,minmax(240px,1fr));gap:16px;padding-top:8px}`,
    `.client-field,.location-field{display:grid;grid-template-columns:1fr auto;gap:8px;align-items:start}`, 
    `mat-checkbox{margin-top:10px}`,
    `@media (max-width: 760px){.client-field,.location-field{grid-template-columns:1fr}}`
  ]
})
export class PolicyFormDialogComponent implements OnInit {
  carriers: Carrier[] = [];
  parameters: Parameter[] = [];
  selectedClient?: Client | null;
  selectedCity?: City | null;

  readonly form = this.fb.group({
    carrierId: [this.data.policy?.carrierId ?? null, Validators.required],
    clientId: [this.data.policy?.clientId ?? null],
    policyNumber: [this.data.policy?.policyNumber ?? ''],
    subscriberId: [this.data.policy?.subscriberId ?? ''],
    effectiveDate: [this.normalizeDate(this.data.policy?.effectiveDate)],
    issueDate: [this.normalizeDate(this.data.policy?.issueDate)],
    terminationDate: [this.normalizeDate(this.data.policy?.terminationDate)],
    statusId: [this.data.policy?.statusId ?? null, Validators.required],
    residentCityId: [this.data.policy?.residentCityId ?? null, Validators.required],
    issueState: [this.data.policy?.issueState ?? ''],
    membersCount: [this.data.policy?.membersCount ?? null],
    sourceKey: [this.data.policy?.sourceKey ?? ''],
    active: [this.data.policy?.active ?? true]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly dialog: MatDialog,
    private readonly carrierService: CarrierService,
    private readonly parameterService: ParameterService,
    public readonly dialogRef: MatDialogRef<PolicyFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: PolicyFormDialogData
  ) {}

  ngOnInit(): void {
    this.carriers = this.data.carriers ?? [];
    this.parameters = this.data.parameters ?? [];

    if (!this.carriers.length) {
      this.carrierService.list().subscribe((carriers) => (this.carriers = carriers));
    }

    if (!this.parameters.length) {
      this.parameterService.list().subscribe((parameters) => (this.parameters = parameters));
    }
  }

  get statusOptions(): Parameter[] {
    const filtered = this.parameters.filter((item) => this.matchesGroup(item, ['POLICY_STATUS', 'STATUS_POLICY', 'ESTADOS_POLIZA']));
    return filtered.length ? filtered : this.parameters;
  }


  get selectedClientLabel(): string {
    if (this.selectedClient) {
      return `${this.selectedClient.externalClientId ?? 'Sin ID'} - ${this.selectedClient.fullName}`;
    }

    if (this.data.policy?.clientName) {
      return this.data.policy.clientName;
    }

    return this.form.controls.clientId.value ? `Cliente ID ${this.form.controls.clientId.value}` : 'Sin cliente seleccionado';
  }

  get selectedCityLabel(): string {
    if (this.selectedCity) {
      return `${this.selectedCity.countryName} / ${this.selectedCity.stateName} / ${this.selectedCity.name}`;
    }

    const policy = this.data.policy;
    if (policy?.residentCityName) {
      const country = policy.residentCountryName ?? '—';
      const state = policy.residentStateName ?? '—';
      return `${country} / ${state} / ${policy.residentCityName}`;
    }

    return this.form.controls.residentCityId.value ? `Ciudad ID ${this.form.controls.residentCityId.value}` : 'Sin ciudad seleccionada';
  }

  openClientSearch(): void {
    const dialogRef = this.dialog.open(ClientSearchDialogComponent, {
      width: '880px',
      maxWidth: '96vw',
      data: { selectedClientId: this.form.controls.clientId.value }
    });

    dialogRef.afterClosed().subscribe((client?: Client) => {
      if (!client) {
        return;
      }

      this.selectedClient = client;
      this.form.controls.clientId.setValue(client.id ?? null);
    });
  }


  openCitySearch(): void {
    const policy = this.data.policy;
    const dialogRef = this.dialog.open(CitySearchDialogComponent, {
      width: '980px',
      maxWidth: '96vw',
      data: {
        selectedCountryId: this.selectedCity?.countryId ?? policy?.residentCountryId ?? null,
        selectedStateId: this.selectedCity?.stateId ?? policy?.residentStateId ?? null,
        selectedCityId: this.form.controls.residentCityId.value
      }
    });

    dialogRef.afterClosed().subscribe((city?: City) => {
      if (!city) {
        return;
      }

      this.selectedCity = city;
      this.form.controls.residentCityId.setValue(city.id ?? null);
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload: Partial<Policy> = {
      carrierId: raw.carrierId,
      clientId: raw.clientId,
      policyNumber: this.emptyToNull(raw.policyNumber),
      subscriberId: this.emptyToNull(raw.subscriberId),
      effectiveDate: this.emptyToNull(raw.effectiveDate),
      issueDate: this.emptyToNull(raw.issueDate),
      terminationDate: this.emptyToNull(raw.terminationDate),
      statusId: raw.statusId,
      residentCityId: raw.residentCityId,
      issueState: this.emptyToNull(raw.issueState),
      membersCount: raw.membersCount,
      sourceKey: this.emptyToNull(raw.sourceKey),
      active: raw.active ?? true
    };

    this.dialogRef.close(payload);
  }

  private matchesGroup(parameter: Parameter, groups: string[]): boolean {
    const group = parameter.parameterGroup?.trim().toUpperCase();
    return groups.includes(group);
  }

  private normalizeDate(value?: string | null): string {
    return value ? value.substring(0, 10) : '';
  }

  private emptyToNull(value: string | null | undefined): string | null {
    return value && value.trim() ? value.trim() : null;
  }
}
