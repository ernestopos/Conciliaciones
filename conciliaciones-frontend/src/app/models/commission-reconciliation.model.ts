export interface CommissionReconciliation {
  commissionStatementItemId: number;
  commissionStatementId?: number | null;

  policyId?: number | null;
  policyNumber?: string | null;
  policyStatusId?: number | null;
  policyStatusName?: string | null;

  clientId?: number | null;
  clientName?: string | null;

  producerId?: number | null;
  producerName?: string | null;

  agencyId?: number | null;
  agencyName?: string | null;

  carrierId?: number | null;
  carrierName?: string | null;

  netAmount?: number | null;
  rate?: number | null;
  commissionRatePct?: number | null;
  estimatedPaymentAmount?: number | null;

  reconciliationType?: string | null;
  reconciliationReason?: string | null;

  createdAt?: string | null;
}

export interface CommissionReconciliationFilters {
  producerName?: string | null;
  policyNumber?: string | null;
  agencyName?: string | null;
  carrierName?: string | null;
}

export interface GenerateCommissionPaymentRequest {
  policyStatusId?: number | null;
  netAmount?: number | null;
  rate?: number | null;
  commissionRatePct?: number | null;
}
