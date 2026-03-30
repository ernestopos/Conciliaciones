import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SourceFileService } from '../../../services/source-file.service';
import { SourceFileModel } from '../../../models/source-file.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { GenericTableComponent } from '../../../shared/components/generic-table/generic-table.component';
import { SearchFilterBarComponent } from '../../../shared/components/search-filter-bar/search-filter-bar.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-source-files-page',
  standalone: true,
  imports: [
    CommonModule,
    LoadingSpinnerComponent,
    EmptyStateComponent,
    GenericTableComponent,
    SearchFilterBarComponent,
    PageHeaderComponent
  ],
  templateUrl: './source-files-page.component.html'
})
export class SourceFilesPageComponent implements OnInit {
  loading = true;
  rows: SourceFileModel[] = [];
  filtered: SourceFileModel[] = [];
  readonly columns = ['id', 'clientId', 'originalFileName', 'fileType', 'processingStatus'];
  readonly headers = { id: 'ID', clientId: 'Cliente', originalFileName: 'Archivo', fileType: 'Tipo', processingStatus: 'Estado' };

  constructor(private readonly service: SourceFileService) {}

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
