import { SafeResourceUrl } from '@angular/platform-browser';

export type ReportFormat = 'PDF';

export interface GenerateReportRequest {
  reportCode: string;
  format: ReportFormat;
  parameters?: Record<string, unknown>;
}

export interface GeneratedReportPreview {
  blob: Blob;
  url: string;
  safeUrl: SafeResourceUrl;
  fileName: string;
}
