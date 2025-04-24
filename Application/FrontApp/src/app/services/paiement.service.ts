import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Paiement} from '../models';


@Injectable({
  providedIn: 'root'
})
export class PaiementService {
  private baseUrl = 'http://localhost:8081/paiements';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(`${this.baseUrl}`);
  }

  getById(id: number): Observable<Paiement> {
    return this.http.get<Paiement>(`${this.baseUrl}/${id}`);
  }

  getByPatient(patientId: number): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(`${this.baseUrl}/patient/${patientId}`);
  }

  create(paiement: Paiement): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}`, paiement , {
      responseType: 'text' as 'json'
    });
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  update(id: number, paiement: Paiement): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, paiement , {
      responseType: 'text' as 'json'
    });

  }


}
