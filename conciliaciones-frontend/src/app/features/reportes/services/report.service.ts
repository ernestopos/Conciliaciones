import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { GenerateReportRequest } from '../models/report.model';

@Injectable({ providedIn: 'root' })
export class ReportService {
  constructor(private readonly http: HttpClient) {}

  generatePdf(reportCode: string, parameters: Record<string, unknown> = { limit: 100 }): Observable<{ blob: Blob; fileName: string }> {
    const payload: GenerateReportRequest = {
      reportCode,
      format: 'PDF',
      parameters
    };

    return this.http.post(`${environment.api.reporting}/reports/generate`, payload, {
      observe: 'response',
      responseType: 'blob'
    }).pipe(
      map((response: HttpResponse<Blob>) => ({
        blob: this.normalizePdfBlob(response.body),
        fileName: this.resolveFileName(response, reportCode)
      }))
    );
  }

  private normalizePdfBlob(blob: Blob | null): Blob {
    if (!blob) {
      return new Blob([], { type: 'application/pdf' });
    }

    if (blob.type === 'application/pdf') {
      return blob;
    }

    return new Blob([blob], { type: 'application/pdf' });
  }

  private resolveFileName(response: HttpResponse<Blob>, reportCode: string): string {
    const contentDisposition = response.headers.get('content-disposition');
    const fileName = contentDisposition?.match(/filename\*=UTF-8''([^;]+)|filename="?([^";]+)"?/i);
    const resolved = decodeURIComponent(fileName?.[1] ?? fileName?.[2] ?? '');

    return resolved || `${reportCode}.pdf`;
  }
}
