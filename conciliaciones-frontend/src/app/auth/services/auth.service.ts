import { Inject, Injectable, InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthUser, LoginCredentials, LoginResult } from '../models/auth.models';
import { AuthProviderPort } from '../ports/auth-provider.port';

export const AUTH_PROVIDER = new InjectionToken<AuthProviderPort>('AUTH_PROVIDER');

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(@Inject(AUTH_PROVIDER) private readonly provider: AuthProviderPort) {}

  login(credentials: LoginCredentials): Observable<LoginResult> {
    return this.provider.login(credentials);
  }

  logout(): void {
    this.provider.logout();
  }

  isAuthenticated(): boolean {
    return this.provider.isAuthenticated();
  }

  getCurrentUser(): AuthUser | null {
    return this.provider.getCurrentUser();
  }
}
