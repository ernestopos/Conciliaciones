import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusChipComponent } from '../../shared/components/status-chip/status-chip.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule, PageHeaderComponent, StatusChipComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
  readonly cards = [
    { label: 'Archivos cargados', value: 198 },
    { label: 'Pólizas', value: 1275 },
    { label: 'Casos de conciliación', value: 86 },
    { label: 'Pagos generados', value: 642 }
  ];

  readonly recentAudit = [
    { event: 'Carga de archivo', user: 'Administrador Demo', status: 'OK' },
    { event: 'Ejecución de procesamiento', user: 'Operador 1', status: 'WARN' },
    { event: 'Aprobación de pago', user: 'Supervisor', status: 'OK' }
  ];

  readonly displayedColumns = ['event', 'user', 'status'];
}
