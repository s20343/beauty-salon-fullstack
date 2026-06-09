import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SalonDetail, SalonRequest, SalonSummary } from '../model/salon.model';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SalonService {
  private readonly apiUrl = `${environment.apiUrl}/salons`;

  constructor(private http: HttpClient) {}

  getSalons(district?: string, serviceType?: string): Observable<SalonSummary[]> {
    // make it async reactive
    let params = new HttpParams();

    if (district) {
      params = params.set('district', district);
    }

    if (serviceType) {
      params = params.set('service', serviceType);
    }
    return this.http.get<SalonSummary[]>(this.apiUrl, { params });
  }

  getSalonById(id: number): Observable<SalonDetail> {
    return this.http.get<SalonDetail>(`${this.apiUrl}/${id}`);
  }

  updateSalon(id: number, data: Partial<SalonRequest>): Observable<SalonDetail> {
    return this.http.put<SalonDetail>(`${this.apiUrl}/${id}`, data);
  }
}
