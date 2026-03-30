import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [MatIconModule],
  template: `<section class="empty"><mat-icon>{{icon}}</mat-icon><h3>{{title}}</h3><p>{{description}}</p></section>`,
  styles: [`.empty{text-align:center;padding:32px;color:#64748b}.empty mat-icon{font-size:38px;width:38px;height:38px;color:#94a3b8}`]
})
export class EmptyStateComponent {
  @Input() icon = 'inbox';
  @Input() title = 'Sin resultados';
  @Input() description = 'No hay información para mostrar.';
}
