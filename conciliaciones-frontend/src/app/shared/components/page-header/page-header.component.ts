import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-page-header',
  standalone: true,
  template: `<header class="page-header"><h1>{{title}}</h1><p>{{subtitle}}</p></header>`,
  styles: [`.page-header{margin-bottom:16px}h1{margin:0;font-size:24px}p{margin:4px 0 0;color:#64748b}`]
})
export class PageHeaderComponent {
  @Input() title = '';
  @Input() subtitle = '';
}
