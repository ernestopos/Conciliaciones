import { Component, Input } from '@angular/core';
import { MatChipsModule } from '@angular/material/chips';

@Component({
  selector: 'app-status-chip',
  standalone: true,
  imports: [MatChipsModule],
  template: `<mat-chip [class]="statusClass">{{ label }}</mat-chip>`,
  styles: [`.status-success{background:#dcfce7;color:#166534}.status-warning{background:#fef9c3;color:#854d0e}.status-error{background:#fee2e2;color:#991b1b}.status-info{background:#dbeafe;color:#1d4ed8}`]
})
export class StatusChipComponent {
  @Input() label = 'N/A';
  @Input() status: 'success' | 'warning' | 'error' | 'info' = 'info';

  get statusClass(): string {
    return `status-${this.status}`;
  }
}
