import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { AuthUser } from '../../auth/models/auth.models';
import { SecurityMenuTree, SecuritySubMenuTree } from '../../models/security-menu.model';
import { SecurityMenuService } from '../../services/security-menu.service';
import { NavItem } from '../models/navigation.models';

@Injectable({ providedIn: 'root' })
export class NavigationService {
  private readonly dashboardItem: NavItem = {
    label: 'Dashboard',
    icon: 'dashboard',
    route: '/dashboard'
  };

  constructor(private readonly securityMenuService: SecurityMenuService) {}

  loadMenuForUser(user: AuthUser | null): Observable<NavItem[]> {
    if (!user) {
      return of([this.dashboardItem]);
    }

    const username = this.resolveUsername(user);
    const isAdmin = this.hasAdminRole(user);

    if (!username) {
      return isAdmin ? this.loadAdminMenu() : of([this.dashboardItem]);
    }

    return this.securityMenuService.findUserTree(username).pipe(
      switchMap((tree) => {
        const hasUserMenu = Array.isArray(tree) && tree.length > 0;

        if (hasUserMenu) {
          return of(this.toNavigationItems(tree));
        }

        return isAdmin ? this.loadAdminMenu() : of([this.dashboardItem]);
      }),
      catchError(() => {
        return isAdmin ? this.loadAdminMenu() : of([this.dashboardItem]);
      })
    );
  }

  private loadAdminMenu(): Observable<NavItem[]> {
    return this.securityMenuService.findAdminTree().pipe(
      map((tree) => this.toNavigationItems(tree ?? [])),
      catchError(() => of([this.dashboardItem]))
    );
  }

  private toNavigationItems(tree: SecurityMenuTree[]): NavItem[] {
    const dynamicItems = tree
      .slice()
      .sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
      .map((menu) => this.toMenuItem(menu))
      .filter((item): item is NavItem => item !== null);

    return [this.dashboardItem, ...dynamicItems];
  }

  private toMenuItem(menu: SecurityMenuTree): NavItem | null {
    const children = (menu.children ?? [])
      .slice()
      .sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
      .map((subMenu) => this.toSubMenuItem(subMenu))
      .filter((item): item is NavItem => item !== null);

    if (children.length > 0) {
      return {
        label: this.resolveLabel(menu),
        icon: menu.icon ?? 'folder_open',
        children
      };
    }

    return null;
  }

  private toSubMenuItem(subMenu: SecuritySubMenuTree): NavItem | null {
    if (!subMenu.route) {
      return null;
    }

    return {
      label: this.resolveLabel(subMenu),
      icon: subMenu.icon ?? 'chevron_right',
      route: subMenu.route
    };
  }

  private resolveLabel(item: SecurityMenuTree | SecuritySubMenuTree): string {
    return item.label ?? item.name ?? item.code ?? 'Sin nombre';
  }

  private resolveUsername(user: AuthUser): string | null {
    return user.username ?? user.name ?? user.email ?? null;
  }

  private hasAdminRole(user: AuthUser): boolean {
    console.log('Usuario autenticado:', user);
    return !!user.roles?.some((role) =>
      role?.code?.trim().toUpperCase() === 'ADMIN' ||
      role?.code?.trim().toUpperCase() === 'ROLE_ADMIN'
    );
  }

  private extractStringValues(value: unknown): string[] {
    if (!value) {
      return [];
    }

    if (typeof value === 'string') {
      return [value];
    }

    if (Array.isArray(value)) {
      return value.flatMap((item) => this.extractStringValues(item));
    }

    if (typeof value === 'object') {
      return Object.values(value as Record<string, unknown>)
        .flatMap((item) => this.extractStringValues(item));
    }

    return [];
  }

  private isAdminRole(role: string): boolean {
    const normalized = role.trim().toUpperCase();
    return normalized === 'ADMIN' || normalized === 'ROLE_ADMIN';
  }

  private readStoredSession(): unknown {
    try {
      const rawSession = localStorage.getItem('conciliaciones_auth_session');
      return rawSession ? JSON.parse(rawSession) : null;
    } catch {
      return null;
    }
  }

  private readStoredTokenPayload(): unknown {
    const token =
      this.readTokenFromSession() ??
      localStorage.getItem('access_token') ??
      localStorage.getItem('token') ??
      sessionStorage.getItem('access_token') ??
      sessionStorage.getItem('token');

    return token ? this.decodeJwtPayload(token) : null;
  }

  private readTokenFromSession(): string | null {
    const session = this.readStoredSession() as any;
    const candidates = [
      session?.token,
      session?.accessToken,
      session?.access_token,
      session?.data?.token,
      session?.data?.accessToken
    ];

    return candidates.find((candidate) => typeof candidate === 'string' && candidate.trim().length > 0) ?? null;
  }

  private decodeJwtPayload(token: string): unknown {
    try {
      const payload = token.split('.')[1];
      if (!payload) {
        return null;
      }

      const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padded = normalized.padEnd(
        normalized.length + (4 - (normalized.length % 4)) % 4,
        '='
      );

      return JSON.parse(atob(padded));
    } catch {
      return null;
    }
  }
}
