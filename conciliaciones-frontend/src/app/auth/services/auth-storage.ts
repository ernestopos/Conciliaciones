import { AuthSession } from '../models/auth.models';

export const AUTH_SESSION_STORAGE_KEY = 'conciliaciones_auth_session';

export function saveAuthSession(session: AuthSession): void {
  localStorage.setItem(AUTH_SESSION_STORAGE_KEY, JSON.stringify(session));
}

export function clearAuthSession(): void {
  localStorage.removeItem(AUTH_SESSION_STORAGE_KEY);
}

export function readAuthSession(): AuthSession | null {
  const raw = localStorage.getItem(AUTH_SESSION_STORAGE_KEY);
  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw) as AuthSession;
  } catch {
    clearAuthSession();
    return null;
  }
}

export function getStoredAccessToken(): string | null {
  return readAuthSession()?.token ?? null;
}
