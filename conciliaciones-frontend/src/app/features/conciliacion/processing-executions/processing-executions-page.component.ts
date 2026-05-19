import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { ProcessingExecutionService } from '../../../services/processing-execution.service';
import { ProcessingExecutionModel } from '../../../models/processing-execution.model';
import { SearchFilterBarComponent } from '../../../shared/components/search-filter-bar/search-filter-bar.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-processing-executions-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    SearchFilterBarComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    PageHeaderComponent
  ],
  templateUrl: './processing-executions-page.component.html',
  styleUrl: './processing-executions-page.component.scss'
})
export class ProcessingExecutionsPageComponent implements OnInit {
  loading = true;
  rows: ProcessingExecutionModel[] = [];
  filtered: ProcessingExecutionModel[] = [];

  readonly columns = ['id', 'sourceFileId', 'originalFileName', 'planExecuteCode', 'statusName', 'startedAt', 'successful', 'message'];
  readonly headers: Record<string, string> = {
    id: 'ID',
    sourceFileId: 'Archivo fuente',
    originalFileName: 'Nombre archivo',
    planExecuteCode: 'Plan',
    statusName: 'Estado',
    startedAt: 'Inicio',
    successful: 'Exitoso',
    message: 'Mensaje'
  };

  constructor(private readonly service: ProcessingExecutionService) {}

  ngOnInit(): void {
    this.loadExecutions();
  }

  applyFilter(query: string): void {
    const q = query.toLowerCase();
    this.filtered = this.rows.filter((item) => JSON.stringify(item).toLowerCase().includes(q));
  }

  displayValue(row: ProcessingExecutionModel, column: string): string {
    const value = (row as any)[column];
    if (value === null || value === undefined || value === '') {
      return '—';
    }
    if (typeof value === 'boolean') {
      return value ? 'Sí' : 'No';
    }
    return String(value);
  }

  private loadExecutions(): void {
    this.loading = true;
    this.service.list().subscribe({
      next: (data) => {
        this.rows = data;
        this.filtered = data;
        this.loading = false;
      },
      error: () => {
        this.rows = [];
        this.filtered = [];
        this.loading = false;
      }
    });
  }
}
