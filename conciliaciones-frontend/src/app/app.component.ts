import { Component } from '@angular/core';
import { TokenRefreshService } from './auth/services/token-refresh.service';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `<router-outlet />`
})
export class AppComponent {
  constructor(private readonly tokenRefreshService: TokenRefreshService) {
    this.tokenRefreshService.start();
  }
}
