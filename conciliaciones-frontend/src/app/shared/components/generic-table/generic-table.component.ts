import { AfterViewInit, Component, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

export interface TableAction<T> {
  label: string;
  icon?: string;
  color?: 'primary' | 'accent' | 'warn';
  callback: (row: T) => void;
}

@Component({
  selector: 'app-generic-table',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatPaginatorModule, MatSortModule, MatButtonModule, MatIconModule],
  templateUrl: './generic-table.component.html',
  styleUrl: './generic-table.component.scss'
})
export class GenericTableComponent<T extends Record<string, any>> implements AfterViewInit, OnChanges {
  @Input() data: T[] = [];
  @Input() displayedColumns: string[] = [];
  @Input() headers: Record<string, string> = {};
  @Input() actions: TableAction<T>[] = [];

  @ViewChild(MatPaginator) paginator?: MatPaginator;
  @ViewChild(MatSort) sort?: MatSort;

  readonly dataSource = new MatTableDataSource<T>([]);

  get renderedColumns(): string[] {
    return this.actions.length ? [...this.displayedColumns, 'actions'] : this.displayedColumns;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data']) {
      this.dataSource.data = this.data;
    }
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator ?? null;
    this.dataSource.sort = this.sort ?? null;
  }

  trackByColumn(_i: number, col: string): string { return col; }

  displayValue(row: T, column: string): string {
    const value = row[column];
    if (value === null || value === undefined || value === '') {
      return '—';
    }
    if (typeof value === 'boolean') {
      return value ? 'Sí' : 'No';
    }
    return String(value);
  }
}
