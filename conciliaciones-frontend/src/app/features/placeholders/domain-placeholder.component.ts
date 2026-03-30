import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-domain-placeholder',
  standalone: true,
  imports: [PageHeaderComponent, EmptyStateComponent],
  template: `
    <app-page-header [title]="title" subtitle="Base de pantalla lista para integrar API real" />
    <app-empty-state icon="construction" [title]="title" description="Vista inicial para demo y navegación." />
  `
})
export class DomainPlaceholderComponent {
  private readonly route = inject(ActivatedRoute);
  readonly title = this.route.snapshot.data['title'] as string;
}
