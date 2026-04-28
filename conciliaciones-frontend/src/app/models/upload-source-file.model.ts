export interface PresignedUploadResponse {
  sourceFileId: number;
  bucketName: string;
  objectKey: string;
  presignedUrl: string;
  expiresInMinutes: number;
}

export interface UploadCallbackRequest {
  sourceFileId: number;
  bucketName: string;
  objectKey: string;
  fileName: string;
  contentType: string;
  fileSizeBytes: number;
  success: boolean;
  errorMessage?: string | null;
}

export interface UploadCallbackResponse {
  sourceFileId: number;
  processingStatusId: number;
  processingStatus: string;
  message: string;
}
