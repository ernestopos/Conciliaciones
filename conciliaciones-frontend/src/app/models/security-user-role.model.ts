export interface SaveSecurityUserRoleRequest {
  userId: number;
  roleId: number;
}

export interface SecurityUserRole {
  id: number;
  userId: number;
  username: string;
  email?: string | null;
  fullName?: string | null;
  roleId: number;
  roleCode?: string | null;
  roleName?: string | null;
  active: boolean;
}
