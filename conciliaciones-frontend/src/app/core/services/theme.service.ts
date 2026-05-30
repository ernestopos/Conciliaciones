import { DOCUMENT } from '@angular/common';
import { Inject, Injectable, signal } from '@angular/core';

export type AppTheme = 'light' | 'dark';

const STORAGE_KEY = 'conciliaciones-theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  readonly theme = signal<AppTheme>(this.loadInitialTheme());

  constructor(@Inject(DOCUMENT) private readonly document: Document) {
    this.applyTheme(this.theme());
  }

  setTheme(theme: AppTheme): void {
    this.theme.set(theme);
    localStorage.setItem(STORAGE_KEY, theme);
    this.applyTheme(theme);
  }

  private loadInitialTheme(): AppTheme {
    const storedTheme = localStorage.getItem(STORAGE_KEY);
    return storedTheme === 'dark' ? 'dark' : 'light';
  }

  private applyTheme(theme: AppTheme): void {
    const body = this.document.body;
    body.classList.remove('theme-light', 'theme-dark');
    body.classList.add(`theme-${theme}`);
  }
}
