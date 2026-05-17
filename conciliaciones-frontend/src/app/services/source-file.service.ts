import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import { SourceFileModel } from '../models/source-file.model';

@Injectable({ providedIn: 'root' })
export class SourceFileService {
  private readonly resourceUrl = `${environment.api.core}/source-files`;

  constructor(private readonly http: HttpClient) {}

  list(): Observable<SourceFileModel[]> {
    return this.http
      .get<unknown>(this.resourceUrl)
      .pipe(map((response) => normalizeCollectionResponse<SourceFileModel>(response)));
  }
}
