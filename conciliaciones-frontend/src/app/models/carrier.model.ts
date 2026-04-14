export interface Carrier {
  id?: number;
  code: string;
  name: string;
  description?: string;
  active: boolean;
  createdAt?: string;
  createdBy?: string;
  updatedAt?: string;
  updatedBy?: string;
}
