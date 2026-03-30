import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { AuthProviderPort } from '../ports/auth-provider.port';
import { AuthUser, LoginCredentials, LoginResult } from '../models/auth.models';

const STORAGE_KEY = 'conciliaciones_demo_auth_user';
const DUMMY_PASSWORD = 'Admin123*';

const DUMMY_USERS: AuthUser[] = [
  {
    id: 1,
    name: 'Administrador Demo',
    email: 'admin@conciliaciones.com',
    roles: [{ code: 'ADMIN', name: 'Administrador' }],
    permissions: [
      { code: 'VIEW_DASHBOARD', label: 'Ver dashboard' },
      { code: 'VIEW_AUDIT', label: 'Ver auditoría' },
      { code: 'VIEW_RECONCILIATIONS', label: 'Ver conciliaciones' },
      { code: 'VIEW_PAYMENTS', label: 'Ver pagos' }
    ]
  }
];

@Injectable({ providedIn: 'root' })
export class DummyAuthProvider extends AuthProviderPort {
  login(credentials: LoginCredentials): Observable<LoginResult> {
    const user = DUMMY_USERS.find((u) => u.email === credentials.username);
    const isValidCredentials = Boolean(user) && credentials.password === DUMMY_PASSWORD;

    if (!isValidCredentials) {
      return of({
        success: false,
        errorMessage: 'Credenciales inválidas. Verifique usuario y contraseña.'
      }).pipe(delay(500));
    }

    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));

    return of({ success: true, user }).pipe(delay(500));
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
  }

  isAuthenticated(): boolean {
    return Boolean(this.getCurrentUser());
  }

  getCurrentUser(): AuthUser | null {
    const user = localStorage.getItem(STORAGE_KEY);
    return user ? (JSON.parse(user) as AuthUser) : null;
  }
}
