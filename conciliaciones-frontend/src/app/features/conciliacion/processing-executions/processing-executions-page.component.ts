import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProcessingExecutionService } from '../../../services/processing-execution.service';
import { ProcessingExecutionModel } from '../../../models/processing-execution.model';
import { GenericTableComponent } from '../../../shared/components/generic-table/generic-table.component';
import { SearchFilterBarComponent } from '../../../shared/components/search-filter-bar/search-filter-bar.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-processing-executions-page',
  standalone: true,
  imports: [
    CommonModule,
    GenericTableComponent,
    SearchFilterBarComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    PageHeaderComponent
  ],
  templateUrl: './processing-executions-page.component.html'
})
export class ProcessingExecutionsPageComponent implements OnInit {
  loading = true;
  rows: ProcessingExecutionModel[] = [];
  filtered: ProcessingExecutionModel[] = [];
  readonly columns = ['id', 'sourceFileId', 'executionId', 'status', 'detailMessage'];
  readonly headers = { id: 'ID', sourceFileId: 'Source File', executionId: 'Execution', status: 'Estado', detailMessage: 'Detalle' };

  constructor(private readonly service: ProcessingExecutionService) {}

  ngOnInit(): void {
    this.service.list().subscribe((data) => {
      this.rows = data;
      this.filtered = data;
      this.loading = false;
    });
  }

  applyFilter(query: string): void {
    const q = query.toLowerCase();
    this.filtered = this.rows.filter((item) => JSON.stringify(item).toLowerCase().includes(q));
  }
}
