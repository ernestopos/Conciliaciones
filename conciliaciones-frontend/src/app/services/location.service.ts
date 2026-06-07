import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { normalizeCollectionResponse } from '../core/services/api-response.utils';
import { City, Country, State } from '../models/location.model';

@Injectable({ providedIn: 'root' })
export class LocationService {
  constructor(private readonly http: HttpClient) {}

  countries(): Observable<Country[]> {
    return this.http
      .get<unknown>(`${environment.api.core}/locations/countries`)
      .pipe(map((response) => normalizeCollectionResponse<Country>(response)));
  }

  states(countryId: number): Observable<State[]> {
    const params = new HttpParams().set('countryId', String(countryId));

    return this.http
      .get<unknown>(`${environment.api.core}/locations/states`, { params })
      .pipe(map((response) => normalizeCollectionResponse<State>(response)));
  }

  searchCities(stateId: number, name?: string): Observable<City[]> {
    let params = new HttpParams().set('stateId', String(stateId)).set('page', '0').set('size', '10');

    if (name?.trim()) {
      params = params.set('name', name.trim());
    }

    return this.http
      .get<unknown>(`${environment.api.core}/locations/cities/search`, { params })
      .pipe(map((response) => normalizeCollectionResponse<City>(response)));
  }
}
