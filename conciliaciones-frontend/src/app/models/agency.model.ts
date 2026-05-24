export interface Agency {
  id?: number;
  carrierId?: number | null;
  carrierName?: string | null;
  externalAgencyId?: string;
  name: string;
  active: boolean;
  createdAt?: string;
  createdBy?: string;
  updatedAt?: string;
  updatedBy?: string;
}
