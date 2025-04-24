import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {RendezVous} from '../models';


@Injectable({
  providedIn: 'root'
})
export class RendezVousService {
  private baseUrl = 'http://localhost:8081/rendezvous'; // adapte l'URL à ton backend

  constructor(private http: HttpClient) {}

  getAll(): Observable<RendezVous[]> {
    return this.http.get<RendezVous[]>(`${this.baseUrl}`);
  }

  getById(id: number): Observable<RendezVous> {
    return this.http.get<RendezVous>(`${this.baseUrl}/${id}`);
  }

  create(rdv: RendezVous): Observable<number> {
    return this.http.post<number>(this.baseUrl, rdv, {
      responseType: 'text' as 'json'
    });
  }

  update(id: number, rdv: RendezVous): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, rdv, {
      responseType: 'text' as 'json'
    });
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getByStatut(statut: string): Observable<RendezVous[]> {
    return this.http.get<RendezVous[]>(`${this.baseUrl}/statut/${statut}`);
  }

  getByDate(date: string): Observable<RendezVous[]> {
    return this.http.get<RendezVous[]>(`${this.baseUrl}/date/${date}`);
  }
}
