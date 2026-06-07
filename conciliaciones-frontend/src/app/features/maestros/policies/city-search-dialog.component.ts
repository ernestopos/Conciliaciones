import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { City, Country, State } from '../../../models/location.model';
import { LocationService } from '../../../services/location.service';

interface CitySearchDialogData {
  selectedCountryId?: number | null;
  selectedStateId?: number | null;
  selectedCityId?: number | null;
}

@Component({
  selector: 'app-city-search-dialog',
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
    MatSelectModule,
    MatTableModule
  ],
  template: `
    <h2 mat-dialog-title>Buscar ciudad</h2>

    <mat-dialog-content>
      <form class="search-form" [formGroup]="form" (ngSubmit)="searchCities()">
        <mat-form-field appearance="outline">
          <mat-label>País</mat-label>
          <mat-select formControlName="countryId" (selectionChange)="onCountryChange($event.value)">
            <mat-option *ngFor="let country of countries" [value]="country.id">{{ country.name }}</mat-option>
          </mat-select>
          <mat-error>Campo requerido</mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Estado</mat-label>
          <mat-select formControlName="stateId" (selectionChange)="onStateChange()">
            <mat-option *ngFor="let state of states" [value]="state.id">{{ state.name }}</mat-option>
          </mat-select>
          <mat-error>Campo requerido</mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Ciudad</mat-label>
          <input matInput formControlName="cityName" placeholder="Ej: Miami" />
        </mat-form-field>

        <button mat-raised-button color="primary" type="submit" [disabled]="form.controls.stateId.invalid">
          <mat-icon>search</mat-icon>
          Buscar
        </button>
      </form>

      @if (loadingCountries || loadingStates || loadingCities) {
        <div class="loading-box">
          <mat-spinner diameter="36" />
        </div>
      } @else {
        <table mat-table [dataSource]="cities" class="results-table">
          <ng-container matColumnDef="countryName">
            <th mat-header-cell *matHeaderCellDef>País</th>
            <td mat-cell *matCellDef="let city">{{ city.countryName }}</td>
          </ng-container>

          <ng-container matColumnDef="stateName">
            <th mat-header-cell *matHeaderCellDef>Estado</th>
            <td mat-cell *matCellDef="let city">{{ city.stateName }}</td>
          </ng-container>

          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef>Ciudad</th>
            <td mat-cell *matCellDef="let city">{{ city.name }}</td>
          </ng-container>

          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef></th>
            <td mat-cell *matCellDef="let city">
              <button mat-button color="primary" type="button" (click)="select(city)">Seleccionar</button>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
        </table>

        @if (!cities.length) {
          <div class="empty-results">Selecciona país, estado y busca una ciudad.</div>
        }
      }
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button type="button" (click)="dialogRef.close()">Cancelar</button>
    </mat-dialog-actions>
  `,
  styles: [
    `.search-form{display:grid;grid-template-columns:1fr 1fr 1fr auto;gap:12px;align-items:start;margin-top:8px}`,
    `.results-table{width:100%;margin-top:12px}`,
    `.loading-box{display:flex;justify-content:center;padding:28px}`,
    `.empty-results{padding:20px;text-align:center;color:rgba(0,0,0,.62)}`,
    `@media (max-width: 960px){.search-form{grid-template-columns:1fr 1fr}}`,
    `@media (max-width: 640px){.search-form{grid-template-columns:1fr}}`
  ]
})
export class CitySearchDialogComponent implements OnInit {
  readonly displayedColumns = ['countryName', 'stateName', 'name', 'actions'];
  readonly form = this.fb.group({
    countryId: [this.data.selectedCountryId ?? null, Validators.required],
    stateId: [this.data.selectedStateId ?? null, Validators.required],
    cityName: ['']
  });

  countries: Country[] = [];
  states: State[] = [];
  cities: City[] = [];
  loadingCountries = false;
  loadingStates = false;
  loadingCities = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly locationService: LocationService,
    public readonly dialogRef: MatDialogRef<CitySearchDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: CitySearchDialogData
  ) {}

  ngOnInit(): void {
    this.loadCountries();
  }

  onCountryChange(countryId: number | null): void {
    this.form.controls.stateId.setValue(null);
    this.states = [];
    this.cities = [];

    if (countryId) {
      this.loadStates(countryId);
    }
  }

  onStateChange(): void {
    this.cities = [];
  }

  searchCities(): void {
    if (this.form.controls.stateId.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    if (!raw.stateId) {
      return;
    }

    this.loadingCities = true;
    this.locationService.searchCities(raw.stateId, raw.cityName ?? '').subscribe({
      next: (cities) => {
        this.cities = cities;
        this.loadingCities = false;
      },
      error: () => {
        this.cities = [];
        this.loadingCities = false;
      }
    });
  }

  select(city: City): void {
    this.dialogRef.close(city);
  }

  private loadCountries(): void {
    this.loadingCountries = true;

    this.locationService.countries().subscribe({
      next: (countries) => {
        this.countries = countries;
        this.loadingCountries = false;

        const countryId = this.form.controls.countryId.value ?? this.findDefaultCountryId(countries);
        if (countryId) {
          this.form.controls.countryId.setValue(countryId);
          this.loadStates(countryId);
        }
      },
      error: () => {
        this.countries = [];
        this.loadingCountries = false;
      }
    });
  }

  private loadStates(countryId: number): void {
    this.loadingStates = true;

    this.locationService.states(countryId).subscribe({
      next: (states) => {
        this.states = states;
        this.loadingStates = false;

        if (this.data.selectedStateId && states.some((state) => state.id === this.data.selectedStateId)) {
          this.form.controls.stateId.setValue(this.data.selectedStateId);
        }
      },
      error: () => {
        this.states = [];
        this.loadingStates = false;
      }
    });
  }

  private findDefaultCountryId(countries: Country[]): number | null {
    return countries.find((country) => country.code === 'US')?.id ?? countries[0]?.id ?? null;
  }
}
