export interface SaveSecurityRoleMenuPermissionRequest {
  subMenuIds: number[];
}

export interface SecurityRoleMenuPermission {
  id: number;
  roleId: number;
  roleCode?: string;
  roleName?: string;
  menuId?: number;
  menuCode?: string;
  subMenuId: number;
  subMenuCode?: string;
  subMenuLabel?: string;
  route?: string;
  active: boolean;
}
