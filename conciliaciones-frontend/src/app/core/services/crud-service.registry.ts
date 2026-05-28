import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CrudResourceKey } from '../models/crud.models';
import { AgencyService } from '../../services/agency.service';
import { CarrierService } from '../../services/carrier.service';
import { ClientService } from '../../services/client.service';
import { ProducerService } from '../../services/producer.service';
import { ParameterService } from '../../services/parameter.service';
import { ReconciliationCaseService } from '../../services/reconciliation-case.service';

export interface CrudDataService<T extends { id?: number | string }> {
  list(): Observable<T[]>;
  getById(id: number | string): Observable<T>;
  create(payload: Partial<T>): Observable<T>;
  update(id: number | string, payload: Partial<T>): Observable<T>;
  delete(id: number | string): Observable<void>;
  reprocess?(id: number | string): Observable<unknown>;
}

@Injectable({ providedIn: 'root' })
export class CrudServiceRegistry {
  constructor(
    private readonly agencyService: AgencyService,
    private readonly carrierService: CarrierService,
    private readonly clientService: ClientService,
    private readonly producerService: ProducerService,
    private readonly parameterService: ParameterService,
    private readonly reconciliationCaseService: ReconciliationCaseService
  ) {}

  get(resourceKey: CrudResourceKey): CrudDataService<any> {
    switch (resourceKey) {
      case 'agencies':
        return this.agencyService;
      case 'carriers':
        return this.carrierService;
      case 'clients':
        return this.clientService;
      case 'producers':
        return this.producerService;
      case 'parameters':
        return this.parameterService;
      case 'reconciliation-cases':
        return this.reconciliationCaseService;
      default:
        throw new Error(`No existe servicio CRUD para ${resourceKey}`);
    }
  }
}
