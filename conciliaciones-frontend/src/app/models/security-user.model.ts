export interface SecurityUser {
  id: string;
  username: string;
  email?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  enabled: boolean;
  role?: string | null;
}

export interface ConfigureSecurityUserRequest {
  username: string;
  email: string;
  fullName: string;
  active: boolean;
}

export interface ConfiguredSecurityUser {
  id: number;
  username: string;
  email?: string | null;
  fullName?: string | null;
  active: boolean;
  createdAt?: string | null;
  createdBy?: string | null;
  updatedAt?: string | null;
  updatedBy?: string | null;
}
