import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseCrudHttpService } from '../core/services/base-crud-http.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import { Client } from '../models/client.model';

@Injectable({ providedIn: 'root' })
export class ClientService extends BaseCrudHttpService<Client> {
  constructor(http: HttpClient) {
    super(http, '/clients');
  }

  search(externalClientId?: string, fullName?: string): Observable<Client[]> {
    let params = new HttpParams().set('page', '0').set('size', '10');

    if (externalClientId?.trim()) {
      params = params.set('externalClientId', externalClientId.trim());
    }

    if (fullName?.trim()) {
      params = params.set('fullName', fullName.trim());
    }

    return this.http
      .get<unknown>(`${environment.api.core}/clients/search`, { params })
      .pipe(map((response) => normalizeCollectionResponse<Client>(response)));
  }
}
