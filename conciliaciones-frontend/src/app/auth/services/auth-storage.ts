import { AuthSession } from '../models/auth.models';

export const AUTH_SESSION_STORAGE_KEY = 'conciliaciones_auth_session';
const EXPIRATION_SAFETY_WINDOW_MS = 30_000;

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
    const session = JSON.parse(raw) as AuthSession;
    if (!session?.token || isSessionExpired(session)) {
      clearAuthSession();
      return null;
    }
    return session;
  } catch {
    clearAuthSession();
    return null;
  }
}

export function getStoredAccessToken(): string | null {
  return readAuthSession()?.token ?? null;
}

export function isSessionExpired(session: AuthSession): boolean {
  const expiresAt = session.expiresAt ?? getJwtExpirationMs(session.token);
  if (!expiresAt) {
    return false;
  }
  return Date.now() >= expiresAt - EXPIRATION_SAFETY_WINDOW_MS;
}

function getJwtExpirationMs(token: string): number | null {
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
    const decoded = JSON.parse(atob(padded));

    return typeof decoded?.exp === 'number' ? decoded.exp * 1000 : null;
  } catch {
    return null;
  }
}
