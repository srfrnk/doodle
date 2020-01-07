import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  apiUrl: any;

  constructor(private http: HttpClient) {
    this.apiUrl = environment.apiUrl;
  }

  crimes() {
    return this.http.get<Crime[]>(`${this.apiUrl}/uk-pol/crimes`);
  }
}

export interface Crime {
  lat: number;
  lon: number;
}
