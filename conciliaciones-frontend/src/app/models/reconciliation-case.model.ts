export interface ReconciliationCase {
  id?: number;
  sourceFileId: number;
  commissionStatementId?: number | null;
  commissionStatementItemId?: number | null;
  carrierId: number;
  policyId?: number | null;
  producerId?: number | null;
  caseTypeId: number;
  severityId: number;
  statusId: number;
  detectedAt: string;
  description: string;
  suggestedAction?: string;
  resolutionNotes?: string;
  resolvedAt?: string;
  resolvedBy?: string;
  createdAt?: string;
  createdBy?: string;
  updatedAt?: string;
  updatedBy?: string;
}
