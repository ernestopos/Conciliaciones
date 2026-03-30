export interface SourceFileModel {
  id: number;
  clientId: number;
  originalFileName: string;
  mimeType: string;
  fileSize: number;
  fileType: string;
  checksum: string;
  processingStatus: string;
}
