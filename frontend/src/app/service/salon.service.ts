import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SalonDetail, SalonRequest, SalonSummary } from '../model/salon.model';

@Injectable({
  providedIn: 'root',
})
export class SalonService {
  private apiUrl = 'http://localhost:8080/api/salons';

  constructor(private http: HttpClient) {}

  getSalons(district?: string): Observable<SalonSummary[]> {
    let url = this.apiUrl;
    if (district) {
      url += `?district=${district}`;
    }
    return this.http.get<SalonSummary[]>(url);
  }

  getSalonById(id: number): Observable<SalonDetail> {
    return this.http.get<SalonDetail>(`${this.apiUrl}/${id}`);
  }

  updateSalon(id: number, data: Partial<SalonRequest>): Observable<SalonDetail> {
    return this.http.put<SalonDetail>(`${this.apiUrl}/${id}`, data);
  }
}
