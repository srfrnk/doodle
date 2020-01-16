import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) {
    this.apiUrl = environment.apiUrl;
  }
  apiUrl: any;

  private convertRentalHome;

  crimes() {
    return this.http.get<Crime[]>(`${this.apiUrl}/uk-pol/crimes`);
  }

  homeRentalPrices() {
    return this.http.get<RentalHome[]>(`${this.apiUrl}/rental-homes/prices`);
  }
}

export interface Crime {
  lat: number;
  lon: number;
}

export interface RentalHome {
  lat: number;
  lon: number;
  weight: number;
}
