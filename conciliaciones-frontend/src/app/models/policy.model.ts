export interface Policy {
  id?: number;
  carrierId: number;
  carrierName?: string | null;
  clientId?: number | null;
  clientName?: string | null;
  policyNumber: string;
  subscriberId?: string | null;
  effectiveDate?: string | null;
  issueDate?: string | null;
  terminationDate?: string | null;
  statusId: number;
  statusName?: string | null;
  residentState?: string | null;
  issueState?: string | null;
  membersCount?: number | null;
  sourceKey?: string | null;
  active: boolean;
  createdAt?: string;
  createdBy?: string;
  updatedAt?: string;
  updatedBy?: string;
}
