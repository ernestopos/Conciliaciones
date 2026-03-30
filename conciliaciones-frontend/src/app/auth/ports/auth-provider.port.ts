import { Observable } from 'rxjs';
import { AuthUser, LoginCredentials, LoginResult } from '../models/auth.models';

/**
 * Contrato de autenticación desacoplado de cualquier proveedor concreto.
 * 
 * Implementación temporal: DummyAuthProvider.
 * Implementación futura: KeycloakAuthProvider.
 */
export abstract class AuthProviderPort {
  abstract login(credentials: LoginCredentials): Observable<LoginResult>;
  abstract logout(): void;
  abstract isAuthenticated(): boolean;
  abstract getCurrentUser(): AuthUser | null;
}
