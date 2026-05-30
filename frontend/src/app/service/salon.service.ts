import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SalonService {
  private apiUrl = 'http://localhost:8080/api/salons';

  constructor(private http: HttpClient) {}

  getSalons(district?: string): Observable<any[]> {
    let url = this.apiUrl;
    if (district) {
      url += `?district=${district}`;
    }
    return this.http.get<any[]>(url);
  }

  getSalonById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  updateSalon(id: number, data: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, data);
  }
}
