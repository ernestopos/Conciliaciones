import { Component, Input } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [MatProgressSpinnerModule],
  template: `<div class="loading-wrapper"><mat-spinner [diameter]="diameter" /></div>`,
  styles: [`.loading-wrapper{display:grid;place-items:center;padding:24px;}`]
})
export class LoadingSpinnerComponent {
  @Input() diameter = 36;
}
