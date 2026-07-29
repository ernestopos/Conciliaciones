import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  SecureBrowserLaunchRequest,
  SecureBrowserLaunchResponse
} from '../models/secure-browser.model';

@Injectable({ providedIn: 'root' })
export class SecureBrowserService {
  private readonly launchUrl = `${environment.api.auth}/secure-browser/launch`;

  constructor(private readonly http: HttpClient) {}

  launchCarrierPortal(carrierPortalId: number): Observable<SecureBrowserLaunchResponse> {
    const request: SecureBrowserLaunchRequest = { carrierPortalId };
    return this.http.post<SecureBrowserLaunchResponse>(this.launchUrl, request);
  }
}
