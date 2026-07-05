import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { Agency } from '../../../models/agency.model';
import { Policy } from '../../../models/policy.model';
import { Producer } from '../../../models/producer.model';
import { AgencyService } from '../../../services/agency.service';
import { CommissionPaymentService } from '../../../services/commission-payment.service';
import { PolicyService } from '../../../services/policy.service';
import { ProducerService } from '../../../services/producer.service';

@Component({
  selector: 'app-manual-commission-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
    EmptyStateComponent,
    LoadingSpinnerComponent,
    PageHeaderComponent
  ],
  template: `
    <div class="page-shell">
      <app-page-header
        title="Conciliación manual"
        subtitle="Creación manual de liquidaciones de comisión para pólizas que no provienen de archivos CSV."
      />

      @if (loading) {
        <app-loading-spinner />
      } @else {
        <form [formGroup]="form" class="manual-form" (ngSubmit)="save()">
          <mat-card appearance="outlined">
            <mat-card-header>
              <mat-card-title>1. Buscar póliza</mat-card-title>
              <mat-card-subtitle>Seleccione la póliza a la que se le va a crear la liquidación manual.</mat-card-subtitle>
            </mat-card-header>
            <mat-card-content>
              <div class="grid two">
                <mat-form-field appearance="outline">
                  <mat-label>Póliza</mat-label>
                  <mat-select formControlName="policyId" (selectionChange)="onPolicySelected()">
                    @for (policy of policies; track policy.id) {
                      <mat-option [value]="policy.id">
                        {{ policy.policyNumber || ('Póliza ID ' + policy.id) }} - {{ policy.clientName || 'Sin cliente' }}
                      </mat-option>
                    }
                  </mat-select>
                  @if (form.controls.policyId.hasError('required')) {
                    <mat-error>La póliza es obligatoria.</mat-error>
                  }
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Filtro rápido</mat-label>
                  <input matInput [value]="policyFilter" (input)="filterPolicies($event)" placeholder="Número de póliza o cliente" />
                </mat-form-field>
              </div>

              @if (selectedPolicy) {
                <div class="summary-card">
                  <div><strong>Póliza:</strong> {{ selectedPolicy.policyNumber || '—' }}</div>
                  <div><strong>Cliente:</strong> {{ selectedPolicy.clientName || '—' }}</div>
                  <div><strong>Carrier:</strong> {{ selectedPolicy.carrierName || selectedPolicy.carrierId || '—' }}</div>
                  <div><strong>Estado:</strong> {{ selectedPolicy.statusName || selectedPolicy.statusId || '—' }}</div>
                </div>
              }
            </mat-card-content>
          </mat-card>

          <mat-card appearance="outlined">
            <mat-card-header>
              <mat-card-title>2. Datos de comisión</mat-card-title>
              <mat-card-subtitle>Datos que se guardarán en statement, statement item y pago de comisión.</mat-card-subtitle>
            </mat-card-header>
            <mat-card-content>
              <div class="grid three">
                <mat-form-field appearance="outline">
                  <mat-label>Producer</mat-label>
                  <mat-select formControlName="producerId" (selectionChange)="onProducerSelected()">
                    @for (producer of producers; track producer.id) {
                      <mat-option [value]="producer.id">{{ producer.fullName || ('Producer ID ' + producer.id) }}</mat-option>
                    }
                  </mat-select>
                  @if (form.controls.producerId.hasError('required')) {
                    <mat-error>El producer es obligatorio.</mat-error>
                  }
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Agencia</mat-label>
                  <mat-select formControlName="agencyId">
                    <mat-option [value]="null">Sin agencia</mat-option>
                    @for (agency of agencies; track agency.id) {
                      <mat-option [value]="agency.id">{{ agency.name || ('Agencia ID ' + agency.id) }}</mat-option>
                    }
                  </mat-select>
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Invoice Number</mat-label>
                  <input matInput formControlName="invoiceNumber" maxlength="80" />
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Statement Date</mat-label>
                  <input matInput type="date" formControlName="statementDate" />
                  @if (form.controls.statementDate.hasError('required')) {
                    <mat-error>La fecha statement es obligatoria.</mat-error>
                  }
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Paid Date</mat-label>
                  <input matInput type="date" formControlName="paidDate" />
                  @if (form.controls.paidDate.hasError('required')) {
                    <mat-error>La fecha de pago es obligatoria.</mat-error>
                  }
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Concepto</mat-label>
                  <input matInput formControlName="concept" maxlength="120" />
                </mat-form-field>
              </div>

              <div class="grid three amounts">
                <mat-form-field appearance="outline">
                  <mat-label>Net Amount</mat-label>
                  <input matInput type="number" step="0.01" formControlName="netAmount" />
                  @if (form.controls.netAmount.invalid) {
                    <mat-error>Debe ser mayor a cero.</mat-error>
                  }
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Rate</mat-label>
                  <input matInput type="number" step="0.01" formControlName="rate" />
                  @if (form.controls.rate.invalid) {
                    <mat-error>Debe ser mayor a cero.</mat-error>
                  }
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Commission %</mat-label>
                  <input matInput type="number" step="0.01" formControlName="commissionRatePct" />
                  @if (form.controls.commissionRatePct.invalid) {
                    <mat-error>Debe ser mayor a cero.</mat-error>
                  }
                </mat-form-field>
              </div>

              <div class="payment-result">
                <div>
                  <span>Pago Producer</span>
                  <strong>{{ formatNumber(paymentAmount) }}</strong>
                </div>
                <mat-checkbox formControlName="includedForPayment">Incluir para pago</mat-checkbox>
              </div>
            </mat-card-content>
          </mat-card>

          <div class="actions">
            <button mat-button type="button" (click)="clear()" [disabled]="saving">Limpiar</button>
            <button mat-raised-button color="primary" type="submit" [disabled]="saving || form.invalid">
              <mat-icon>save</mat-icon>
              Guardar liquidación
            </button>
          </div>
        </form>
      }
    </div>
  `,
  styles: [
    `.page-shell{display:block}`,
    `.manual-form{display:flex;flex-direction:column;gap:16px}`,
    `.grid{display:grid;gap:12px;margin-top:16px}`,
    `.grid.two{grid-template-columns:repeat(2,minmax(220px,1fr))}`,
    `.grid.three{grid-template-columns:repeat(3,minmax(180px,1fr))}`,
    `.summary-card{display:grid;grid-template-columns:repeat(4,minmax(140px,1fr));gap:10px;background:rgba(0,0,0,.03);border-radius:10px;padding:12px;margin-top:4px}`,
    `.payment-result{display:flex;justify-content:space-between;align-items:center;gap:16px;border:1px solid rgba(0,0,0,.12);border-radius:12px;padding:14px 16px;margin-top:4px}`,
    `.payment-result span{display:block;font-size:12px;opacity:.75;margin-bottom:4px}`,
    `.payment-result strong{font-size:24px}`,
    `.actions{display:flex;justify-content:flex-end;gap:10px}`,
    `@media(max-width:980px){.grid.three,.summary-card{grid-template-columns:repeat(2,minmax(180px,1fr))}}`,
    `@media(max-width:640px){.grid.two,.grid.three,.summary-card{grid-template-columns:1fr}.payment-result{align-items:flex-start;flex-direction:column}}`
  ]
})
export class ManualCommissionPageComponent implements OnInit {
  loading = true;
  saving = false;
  policyFilter = '';

  allPolicies: Policy[] = [];
  policies: Policy[] = [];
  producers: Producer[] = [];
  agencies: Agency[] = [];
  selectedPolicy: Policy | null = null;

  readonly form = this.fb.group({
    policyId: [null as number | null, Validators.required],
    producerId: [null as number | null, Validators.required],
    agencyId: [null as number | null],
    statementDate: [this.today(), Validators.required],
    paidDate: [this.today(), Validators.required],
    invoiceNumber: [''],
    concept: ['MANUAL'],
    netAmount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    rate: [null as number | null, [Validators.required, Validators.min(0.01)]],
    commissionRatePct: [null as number | null, [Validators.required, Validators.min(0.01)]],
    includedForPayment: [true]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly policyService: PolicyService,
    private readonly producerService: ProducerService,
    private readonly agencyService: AgencyService,
    private readonly commissionPaymentService: CommissionPaymentService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadCatalogs();
  }

  get paymentAmount(): number {
    const netAmount = this.toNumber(this.form.controls.netAmount.value);
    const rate = this.toNumber(this.form.controls.rate.value);
    const commissionRatePct = this.toNumber(this.form.controls.commissionRatePct.value);

    if (netAmount <= 0 || rate <= 0 || commissionRatePct <= 0) {
      return 0;
    }

    return Math.round(((netAmount * rate * commissionRatePct) / 10000) * 100) / 100;
  }

  filterPolicies(event: Event): void {
    const value = (event.target as HTMLInputElement).value ?? '';
    this.policyFilter = value;
    const normalized = value.trim().toLowerCase();

    if (!normalized) {
      this.policies = [...this.allPolicies];
      return;
    }

    this.policies = this.allPolicies.filter((policy) =>
      [policy.policyNumber, policy.clientName, policy.carrierName, policy.statusName]
        .filter(Boolean)
        .some((field) => String(field).toLowerCase().includes(normalized))
    );
  }

  onPolicySelected(): void {
    const policyId = this.form.controls.policyId.value;
    this.selectedPolicy = this.allPolicies.find((policy) => policy.id === policyId) ?? null;
  }

  onProducerSelected(): void {
    const producerId = this.form.controls.producerId.value;
    const producer = this.producers.find((item) => item.id === producerId);

    if (producer?.agencyId && !this.form.controls.agencyId.value) {
      this.form.controls.agencyId.setValue(producer.agencyId);
    }
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const request = {
      policyId: Number(raw.policyId),
      producerId: Number(raw.producerId),
      agencyId: raw.agencyId == null ? null : Number(raw.agencyId),
      statementDate: String(raw.statementDate),
      paidDate: String(raw.paidDate),
      invoiceNumber: raw.invoiceNumber?.trim() || null,
      concept: raw.concept?.trim() || 'MANUAL',
      netAmount: this.toNumber(raw.netAmount),
      rate: this.toNumber(raw.rate),
      commissionRatePct: this.toNumber(raw.commissionRatePct),
      includedForPayment: raw.includedForPayment === true
    };

    this.saving = true;

    this.commissionPaymentService.createManual(request).subscribe({
      next: () => {
        this.saving = false;
        this.snackBar.open('Liquidación manual creada correctamente.', 'Cerrar', { duration: 2600 });
        this.clear();
      },
      error: (error) => {
        this.saving = false;
        const message = error?.error?.message || 'No fue posible crear la liquidación manual.';
        this.snackBar.open(message, 'Cerrar', { duration: 4200 });
      }
    });
  }

  clear(): void {
    this.policyFilter = '';
    this.policies = [...this.allPolicies];
    this.selectedPolicy = null;
    this.form.reset({
      policyId: null,
      producerId: null,
      agencyId: null,
      statementDate: this.today(),
      paidDate: this.today(),
      invoiceNumber: '',
      concept: 'MANUAL',
      netAmount: null,
      rate: null,
      commissionRatePct: null,
      includedForPayment: true
    });
  }

  formatNumber(value: unknown): string {
    const numberValue = Number(value ?? 0);
    if (Number.isNaN(numberValue)) {
      return '0.00';
    }
    return new Intl.NumberFormat('en-US', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(numberValue);
  }

  private loadCatalogs(): void {
    this.loading = true;

    forkJoin({
      policies: this.policyService.list(),
      producers: this.producerService.list(),
      agencies: this.agencyService.list()
    }).subscribe({
      next: ({ policies, producers, agencies }) => {
        this.allPolicies = policies;
        this.policies = policies;
        this.producers = producers.filter((producer) => producer.active !== false);
        this.agencies = agencies.filter((agency) => agency.active !== false);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.allPolicies = [];
        this.policies = [];
        this.producers = [];
        this.agencies = [];
        this.snackBar.open('No fue posible cargar la información para la conciliación manual.', 'Cerrar', { duration: 3600 });
      }
    });
  }

  private today(): string {
    return new Date().toISOString().slice(0, 10);
  }

  private toNumber(value: unknown): number {
    const numberValue = Number(value);
    return Number.isNaN(numberValue) ? 0 : numberValue;
  }
}
