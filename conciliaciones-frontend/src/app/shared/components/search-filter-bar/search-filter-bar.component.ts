import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-search-filter-bar',
  standalone: true,
  imports: [ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatIconModule, MatInputModule],
  template: `
    <form [formGroup]="form" class="search" (ngSubmit)="apply()">
      <mat-form-field appearance="outline">
        <mat-label>Buscar</mat-label>
        <input matInput formControlName="query" placeholder="ID, nombre, estado..." />
        <mat-icon matSuffix>search</mat-icon>
      </mat-form-field>
      <button mat-stroked-button type="submit">Aplicar</button>
      <button mat-button type="button" (click)="clear()">Limpiar</button>
    </form>
  `,
  styles: [`.search{display:flex;gap:12px;align-items:center;margin-bottom:12px}.search mat-form-field{flex:1}`]
})
export class SearchFilterBarComponent {
  @Output() filterChange = new EventEmitter<string>();
  readonly form = this.fb.group({ query: [''] });

  constructor(private readonly fb: FormBuilder) {}

  apply(): void { this.filterChange.emit(this.form.value.query?.trim() ?? ''); }
  clear(): void { this.form.patchValue({ query: '' }); this.filterChange.emit(''); }
}
