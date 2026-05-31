import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';
import { SalonDetail, SalonRequest, SalonSummary } from '../model/salon.model';
import { environment } from '../environments/environment';


@Injectable({
  providedIn: 'root',
})
export class SalonService {
  private readonly apiUrl = environment.apiUrl;
  private salonsCache: SalonSummary[] | null = null;

  constructor(private http: HttpClient) {}

  getSalons(district?: string, serviceType?: string): Observable<SalonSummary[]> {
    let params = new HttpParams();

    if (district) {
      params = params.set('district', district);
    }

    if (serviceType) {
      params = params.set('service', serviceType);
    }

    if (!district && !serviceType) {
      if (this.salonsCache) {
        return of(this.salonsCache);
      }

      return this.http
        .get<SalonSummary[]>(this.apiUrl)
        .pipe(tap((data) => (this.salonsCache = data)));
    }

    return this.http.get<SalonSummary[]>(this.apiUrl, { params });
  }

  getSalonById(id: number): Observable<SalonDetail> {
    return this.http.get<SalonDetail>(`${this.apiUrl}/${id}`);
  }

  updateSalon(id: number, data: Partial<SalonRequest>): Observable<SalonDetail> {
    return this.http.put<SalonDetail>(`${this.apiUrl}/${id}`, data).pipe(
      tap((updatedSalon) => {
        if (!this.salonsCache) return;

        this.salonsCache = this.salonsCache.map((salon) =>
          salon.id === id ? { ...salon, ...updatedSalon } : salon,
        );
      }),
    );
  }
}
