export interface Client {
  id?: number;
  externalClientId?: string;
  firstName?: string;
  middleName?: string;
  lastName?: string;
  fullName: string;
  birthDate?: string;
  state?: string;
  active: boolean;
  createdAt?: string;
  createdBy?: string;
  updatedAt?: string;
  updatedBy?: string;
}
