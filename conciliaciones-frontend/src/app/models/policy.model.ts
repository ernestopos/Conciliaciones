export interface Policy {
  id?: number;
  carrierId: number | null;
  clientId?: number | null;
  policyNumber?: string | null;
  subscriberId?: string | null;
  effectiveDate?: string | null;
  issueDate?: string | null;
  terminationDate?: string | null;
  statusId: number | null;
  statusName?: string | null;
  residentCityId: number | null;
  residentCityName?: string | null;
  residentStateId?: number | null;
  residentStateName?: string | null;
  residentCountryId?: number | null;
  residentCountryName?: string | null;
  issueState?: string | null;
  membersCount?: number | null;
  sourceKey?: string | null;
  active: boolean;
  createdAt?: string;
  createdBy?: string;
  updatedAt?: string;
  updatedBy?: string;

  carrierName?: string;
  clientName?: string;
}
