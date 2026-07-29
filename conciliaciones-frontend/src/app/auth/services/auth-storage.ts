import { AuthSession } from '../models/auth.models';

export const AUTH_SESSION_STORAGE_KEY = 'conciliaciones_auth_session';
const EXPIRATION_SAFETY_WINDOW_MS = 30_000;

export function saveAuthSession(session: AuthSession): void {
  localStorage.setItem(AUTH_SESSION_STORAGE_KEY, JSON.stringify(session));
}

export function clearAuthSession(): void {
  localStorage.removeItem(AUTH_SESSION_STORAGE_KEY);
}

export function readStoredAuthSession(): AuthSession | null {
  const raw = localStorage.getItem(AUTH_SESSION_STORAGE_KEY);
  if (!raw) {
    return null;
  }

  try {
    const session = JSON.parse(raw) as AuthSession;
    if (!session?.token) {
      clearAuthSession();
      return null;
    }
    return session;
  } catch {
    clearAuthSession();
    return null;
  }
}

export function readAuthSession(): AuthSession | null {
  const session = readStoredAuthSession();
  if (!session) {
    return null;
  }

  // Si el access token venció, pero existe refresh token, la sesión todavía
  // puede renovarse. No la borramos aquí para evitar sacar al usuario
  // mientras está trabajando en la aplicación.
  if (isSessionExpired(session) && !session.refreshToken) {
    clearAuthSession();
    return null;
  }

  return session;
}

export function getStoredAccessToken(): string | null {
  return readStoredAuthSession()?.token ?? null;
}

export function isSessionExpired(session: AuthSession): boolean {
  const expiresAt = session.expiresAt ?? getJwtExpirationMs(session.token);
  if (!expiresAt) {
    return false;
  }
  return Date.now() >= expiresAt - EXPIRATION_SAFETY_WINDOW_MS;
}

export function getJwtExpirationMs(token: string): number | null {
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
