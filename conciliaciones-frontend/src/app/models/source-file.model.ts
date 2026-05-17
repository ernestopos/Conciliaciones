export interface SourceFileModel {
  id: number;
  carrierId: number;
  originalFileName: string;
  storedFileName?: string | null;
  fileExtension?: string | null;
  mimeType?: string | null;
  fileSizeBytes?: number | null;
  s3Bucket?: string | null;
  s3Key?: string | null;
  checksum?: string | null;
  sourceSystem?: string | null;
  uploadDate: string;
  uploadedBy?: string | null;
  processingStatusId: number;
  errorMessage?: string | null;
  totalRows?: number | null;
  processedRows?: number | null;
  failedRows?: number | null;
}
