export interface Country {
  id: number;
  code: string;
  name: string;
}

export interface State {
  id: number;
  countryId: number;
  code: string;
  name: string;
}

export interface City {
  id: number;
  name: string;
  stateId: number;
  stateName: string;
  countryId: number;
  countryName: string;
}
