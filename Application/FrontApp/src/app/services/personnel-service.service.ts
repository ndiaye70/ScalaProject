import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import {Garde, Personnel} from '../models';

@Injectable({
  providedIn: 'root'
})
export class PersonnelServiceService {

  private readonly baseUrl = 'http://localhost:8081/personnel';

  private readonly gardeUrl = 'http://localhost:8081/gardes';

  constructor(private http: HttpClient) {}

  getAllPersonnel(): Observable<Personnel[]> {
    return this.http.get<Personnel[]>(this.baseUrl);
  }

  getPersonnelById(id: number): Observable<Personnel> {
    return this.http.get<Personnel>(`${this.baseUrl}/${id}`);
  }

  createPersonnel(personnel: Personnel): Observable<number> {
    return this.http.post<number>(this.baseUrl, personnel , {
      responseType: 'text' as 'json'
    });
  }

  updatePersonnel(id: number, personnel: Personnel): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, personnel , {
      responseType: 'text' as 'json'
    });
  }

  deletePersonnel(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getPersonnelByType(type: string): Observable<Personnel[]> {
    return this.http.get<Personnel[]>(`${this.baseUrl}?type=${type}`);
  }

  getPersonnelBySpecialite(specialite: string): Observable<Personnel[]> {
    return this.http.get<Personnel[]>(`${this.baseUrl}?specialite=${specialite}`);
  }

  getMedecins(): Observable<Personnel[]> {
    return this.http.get<Personnel[]>(`${this.baseUrl}/medecins`);
  }

  getInfirmiers(): Observable<Personnel[]> {
    return this.http.get<Personnel[]>(`${this.baseUrl}/infirmiers`);
  }

  // 📦 Obtenir toutes les gardes
  getAllGardes(): Observable<Garde[]> {
    return this.http.get<Garde[]>(this.gardeUrl);
  }
  getGardeById(id: number): Observable<Garde> {
    return this.http.get<Garde>(`${this.gardeUrl}/${id}`);
  }

// 🔍 Obtenir les gardes d'un personnel
  getGardesParPersonnel(personnelId: number): Observable<Garde[]> {
    return this.http.get<Garde[]>(`${this.gardeUrl}?personnelId=${personnelId}`);
  }

// 📅 Obtenir les gardes entre deux dates
  getGardesParPeriode(debut: string, fin: string): Observable<Garde[]> {
    return this.http.get<Garde[]>(`${this.gardeUrl}?debut=${debut}&fin=${fin}`);
  }

// ➕ Planifier une nouvelle garde
  planifierGarde(garde: Garde): Observable<{ id: number }> {
    return this.http.post<{ id: number }>(this.gardeUrl, garde);
  }

// ✏️ Modifier une garde
  modifierGarde(id: number, garde: Garde): Observable<string> {
    return this.http.put(`${this.gardeUrl}/${id}`, garde, { responseType: 'text' });
  }

// ❌ Supprimer une garde
  supprimerGarde(id: number): Observable<string> {
    return this.http.delete(`${this.gardeUrl}/${id}`, { responseType: 'text' });
  }

}
