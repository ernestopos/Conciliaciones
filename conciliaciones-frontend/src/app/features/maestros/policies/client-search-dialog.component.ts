import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { Client } from '../../../models/client.model';
import { ClientService } from '../../../services/client.service';

interface ClientSearchDialogData {
  selectedClientId?: number | null;
}

@Component({
  selector: 'app-client-search-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatTableModule
  ],
  template: `
    <h2 mat-dialog-title>Buscar cliente</h2>

    <mat-dialog-content>
      <form class="search-form" [formGroup]="form" (ngSubmit)="search()">
        <mat-form-field appearance="outline">
          <mat-label>ID externo</mat-label>
          <input matInput formControlName="externalClientId" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Nombre completo</mat-label>
          <input matInput formControlName="fullName" />
        </mat-form-field>

        <button mat-raised-button color="primary" type="submit">
          <mat-icon>search</mat-icon>
          Buscar
        </button>
      </form>

      @if (loading) {
        <div class="loading-box">
          <mat-spinner diameter="36" />
        </div>
      } @else {
        <table mat-table [dataSource]="clients" class="results-table">
          <ng-container matColumnDef="externalClientId">
            <th mat-header-cell *matHeaderCellDef>ID externo</th>
            <td mat-cell *matCellDef="let client">{{ client.externalClientId || '—' }}</td>
          </ng-container>

          <ng-container matColumnDef="fullName">
            <th mat-header-cell *matHeaderCellDef>Nombre completo</th>
            <td mat-cell *matCellDef="let client">{{ client.fullName }}</td>
          </ng-container>

          <ng-container matColumnDef="state">
            <th mat-header-cell *matHeaderCellDef>Estado</th>
            <td mat-cell *matCellDef="let client">{{ client.state || '—' }}</td>
          </ng-container>

          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef></th>
            <td mat-cell *matCellDef="let client">
              <button mat-button color="primary" type="button" (click)="select(client)">Seleccionar</button>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
        </table>

        @if (!clients.length) {
          <div class="empty-results">No hay clientes para los filtros ingresados.</div>
        }
      }
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button type="button" (click)="dialogRef.close()">Cancelar</button>
    </mat-dialog-actions>
  `,
  styles: [
    `.search-form{display:grid;grid-template-columns:1fr 1fr auto;gap:12px;align-items:start;margin-top:8px}`,
    `.results-table{width:100%;margin-top:12px}`,
    `.loading-box{display:flex;justify-content:center;padding:28px}`,
    `.empty-results{padding:20px;text-align:center;color:rgba(0,0,0,.62)}`,
    `@media (max-width: 760px){.search-form{grid-template-columns:1fr}}`
  ]
})
export class ClientSearchDialogComponent {
  readonly displayedColumns = ['externalClientId', 'fullName', 'state', 'actions'];
  readonly form = this.fb.group({
    externalClientId: [''],
    fullName: ['']
  });

  clients: Client[] = [];
  loading = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly clientService: ClientService,
    public readonly dialogRef: MatDialogRef<ClientSearchDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: ClientSearchDialogData
  ) {
    this.search();
  }

  search(): void {
    const filters = this.form.getRawValue();
    this.loading = true;

    this.clientService.search(filters.externalClientId ?? '', filters.fullName ?? '').subscribe({
      next: (clients) => {
        this.clients = clients;
        this.loading = false;
      },
      error: () => {
        this.clients = [];
        this.loading = false;
      }
    });
  }

  select(client: Client): void {
    this.dialogRef.close(client);
  }
}
