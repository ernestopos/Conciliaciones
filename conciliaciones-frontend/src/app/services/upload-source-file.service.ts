import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpHeaders, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import {
  PresignedUploadResponse,
  UploadCallbackRequest,
  UploadCallbackResponse
} from '../models/upload-source-file.model';

@Injectable({
  providedIn: 'root'
})
export class UploadSourceFileService {
  private readonly storageUrl = `${environment.api.fileManagement}/storage`;

  constructor(private readonly http: HttpClient) {}

  generatePresignedUrl(file: File, folder?: string): Observable<PresignedUploadResponse> {
    return this.http.post<PresignedUploadResponse>(`${this.storageUrl}/presigned-url`, {
      fileName: file.name,
      contentType: file.type || 'application/octet-stream',
      folder: folder?.trim() || null,
      createBucketPerUpload: true
    });
  }

  uploadFile(presignedUrl: string, file: File): Observable<HttpEvent<unknown>> {
    const request = new HttpRequest('PUT', presignedUrl, file, {
      reportProgress: true,
      headers: new HttpHeaders({
        'Content-Type': file.type || 'application/octet-stream'
      })
    });

    return this.http.request(request);
  }

  confirmUploadCallback(request: UploadCallbackRequest): Observable<UploadCallbackResponse> {
    return this.http.post<UploadCallbackResponse>(
      `${this.storageUrl}/upload-callback`,
      request
    );
  }
}
