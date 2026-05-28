export interface Producer {
  id?: number;
  agencyId?: number | null;
  externalProducerId?: string;
  firstName?: string;
  lastName?: string;
  fullName: string;
  email?: string;
  phone?: string;
  npn?: string;
  taxIdMasked?: string;
  active: boolean;
  createdAt?: string;
  createdBy?: string;
  updatedAt?: string;
  updatedBy?: string;
}
