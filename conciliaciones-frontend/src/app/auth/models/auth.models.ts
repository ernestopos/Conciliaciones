export interface Permission {
  code: string;
  label: string;
}

export interface Role {
  code: string;
  name: string;
}

export interface AuthUser {
  id: number;
  name: string;
  email: string;
  roles: Role[];
  permissions: Permission[];
}

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface LoginResult {
  success: boolean;
  token?: string;
  refreshToken?: string;
  user?: AuthUser;
  expiresIn?: number;
  expiresAt?: number;
  errorMessage?: string;
}

export interface AuthSession {
  token: string;
  refreshToken?: string;
  user: AuthUser;
  expiresIn?: number;
  expiresAt?: number;
}
