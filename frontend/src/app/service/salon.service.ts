import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';
import { SalonDetail, SalonRequest, SalonSummary } from '../model/salon.model';

@Injectable({
  providedIn: 'root',
})
export class SalonService {

  private apiUrl = 'http://localhost:8080/api/salons';

  private salonsCache: SalonSummary[] | null = null;

  constructor(private http: HttpClient) {}

  getSalons(district?: string): Observable<SalonSummary[]> {

    if (district) {
      return this.http.get<SalonSummary[]>(`${this.apiUrl}?district=${district}`);
    }
    if (this.salonsCache) {
      return of(this.salonsCache);
    }

    return this.http.get<SalonSummary[]>(this.apiUrl).pipe(
      tap((data) => {
        this.salonsCache = data;
      }),
    );
  }

  getSalonById(id: number): Observable<SalonDetail> {
    return this.http.get<SalonDetail>(`${this.apiUrl}/${id}`);
  }

  updateSalon(id: number, data: Partial<SalonRequest>): Observable<SalonDetail> {
    return this.http.put<SalonDetail>(`${this.apiUrl}/${id}`, data).pipe(
      tap((updatedSalonFromBackend) => {

        if (this.salonsCache !== null) {
          for (let i = 0; i < this.salonsCache.length; i++) {
            if (this.salonsCache[i].id === id) {
              this.salonsCache[i].name = updatedSalonFromBackend.name;
              this.salonsCache[i].address = updatedSalonFromBackend.address;
              this.salonsCache[i].district = updatedSalonFromBackend.district;
              this.salonsCache[i].priceRange = updatedSalonFromBackend.priceRange;
            }
          }
        }
      }),
    );
  }
}
