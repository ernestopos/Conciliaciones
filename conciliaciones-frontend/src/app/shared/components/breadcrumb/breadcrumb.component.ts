import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-breadcrumb',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  template: `
    <nav class="breadcrumb">
      <ng-container *ngFor="let item of items; let last = last">
        <span>{{ item }}</span>
        <mat-icon *ngIf="!last">chevron_right</mat-icon>
      </ng-container>
    </nav>
  `,
  styles: [`.breadcrumb{display:flex;align-items:center;gap:6px;font-size:12px;color:#64748b;margin-bottom:8px}.breadcrumb mat-icon{font-size:16px;width:16px;height:16px}`]
})
export class BreadcrumbComponent {
  @Input() items: string[] = [];
}
