export interface CommissionPayment {
  id: number;
  policyId?: number | null;
  policyName?: string | null;
  commissionStatementId?: number | null;
  producerId?: number | null;
  producerName?: string | null;
  agencyId?: number | null;
  agencyName?: string | null;
  carrierId?: number | null;
  carrierName?: string | null;
  netAmount?: number | null;
  rate?: number | null;
  commissionRatePct?: number | null;
  paymentAmount?: number | null;
  includedForPayment?: boolean | null;
  createdAt?: string | null;
}

export interface CommissionPaymentFilters {
  producerName?: string | null;
  policyNumber?: string | null;
  agencyName?: string | null;
  carrierName?: string | null;
}


export interface RecalculateCommissionPaymentRequest {
  netAmount: number;
  rate: number;
  commissionRatePct: number;
  includedForPayment: boolean;
}


export interface CreateManualCommissionPaymentRequest {
  policyId: number;
  producerId: number;
  agencyId?: number | null;
  statementDate: string;
  paidDate: string;
  invoiceNumber?: string | null;
  concept?: string | null;
  netAmount: number;
  rate: number;
  commissionRatePct: number;
  includedForPayment: boolean;
}
