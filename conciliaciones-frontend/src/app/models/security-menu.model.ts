export interface SecuritySubMenuTree {
  id: number;
  code?: string;
  name?: string;
  label?: string;
  route?: string;
  icon?: string;
  sortOrder?: number;
}

export interface SecurityMenuTree {
  id: number;
  code?: string;
  name?: string;
  label?: string;
  icon?: string;
  sortOrder?: number;
  children?: SecuritySubMenuTree[];
}
