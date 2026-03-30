import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { AuthService } from '../../../auth/services/auth.service';
import { NavigationService } from '../../../core/services/navigation.service';
import { SideMenuComponent } from '../side-menu/side-menu.component';
import { TopToolbarComponent } from '../top-toolbar/top-toolbar.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, MatSidenavModule, SideMenuComponent, TopToolbarComponent],
  templateUrl: './app-layout.component.html',
  styleUrl: './app-layout.component.scss'
})
export class AppLayoutComponent {
  readonly menu = this.navigationService.menu;
  readonly sidenavOpen = signal(true);
  readonly userName = computed(() => this.authService.getCurrentUser()?.name ?? 'Invitado');

  constructor(
    private readonly navigationService: NavigationService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  toggleMenu(): void {
    this.sidenavOpen.set(!this.sidenavOpen());
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
