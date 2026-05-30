import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { AppTheme, ThemeService } from '../../../core/services/theme.service';

@Component({
  selector: 'app-top-toolbar',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatMenuModule],
  templateUrl: './top-toolbar.component.html',
  styleUrl: './top-toolbar.component.scss'
})
export class TopToolbarComponent {
  @Input() userName = 'Invitado';
  @Output() menuToggle = new EventEmitter<void>();
  @Output() logoutClick = new EventEmitter<void>();

  readonly currentTheme = this.themeService.theme;

  constructor(private readonly themeService: ThemeService) {}

  setTheme(theme: AppTheme): void {
    this.themeService.setTheme(theme);
  }
}
