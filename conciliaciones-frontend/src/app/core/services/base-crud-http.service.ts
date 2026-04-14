import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { normalizeCollectionResponse } from './api-response.utils';

export abstract class BaseCrudHttpService<T extends { id?: number | string }> {
  protected constructor(
    protected readonly http: HttpClient,
    private readonly resourcePath: string
  ) {}

  list(): Observable<T[]> {
    return this.http
      .get<unknown>(`${environment.api.core}${this.resourcePath}`)
      .pipe(map((response) => normalizeCollectionResponse<T>(response)));
  }

  getById(id: number | string): Observable<T> {
    return this.http.get<T>(`${environment.api.core}${this.resourcePath}/${id}`);
  }

  create(payload: Partial<T>): Observable<T> {
    return this.http.post<T>(`${environment.api.core}${this.resourcePath}`, payload);
  }

  update(id: number | string, payload: Partial<T>): Observable<T> {
    return this.http.put<T>(`${environment.api.core}${this.resourcePath}/${id}`, payload);
  }

  delete(id: number | string): Observable<void> {
    return this.http.delete<void>(`${environment.api.core}${this.resourcePath}/${id}`);
  }
}
