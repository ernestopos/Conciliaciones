export interface Parameter {
  id?: number;
  name: string;
  description?: string;
  value: string;
  parameterGroup: string;
  active: boolean;
  sortOrder: number;
  createdAt?: string;
  createdBy?: string;
  updatedAt?: string;
  updatedBy?: string;
}
