import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import { CarrierPortal } from '../models/carrier-portal.model';

@Injectable({ providedIn: 'root' })
export class CarrierPortalService {
  private readonly resourceUrl = `${environment.api.core}/carrier-portals`;

  constructor(private readonly http: HttpClient) {}

  listActive(): Observable<CarrierPortal[]> {
    return this.http
      .get<unknown>(this.resourceUrl)
      .pipe(map((response) => normalizeCollectionResponse<CarrierPortal>(response)));
  }

  getById(id: number): Observable<CarrierPortal> {
    return this.http.get<CarrierPortal>(`${this.resourceUrl}/${id}`);
  }
}
