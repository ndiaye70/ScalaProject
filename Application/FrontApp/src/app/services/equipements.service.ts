// src/app/services/equipements.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Chambre, Lit, Materiel} from '../models';



@Injectable({
  providedIn: 'root'
})
export class EquipementsService {

  private readonly baseUrl = 'http://localhost:8081';

  private base2Url = 'http://localhost:8081/materiels';

  constructor(private http: HttpClient) {}

  // 🔹 Lits
  getLitById(id: number): Observable<Lit> {
    return this.http.get<Lit>(`${this.baseUrl}/lits/${id}`);
  }

  getLitsByChambreId(chambreId: number): Observable<Lit[]> {
    return this.http.get<Lit[]>(`${this.baseUrl}/lits/chambre/${chambreId}`);
  }

  getLitsByStatut(statut: string): Observable<Lit[]> {
    return this.http.get<Lit[]>(`${this.baseUrl}/lits/statut/${statut}`);
  }

  searchLitByNumero(numero: string): Observable<Lit[]> {
    return this.http.get<Lit[]>(`${this.baseUrl}/lits/search?numero=${numero}`);
  }

  // 🔹 Chambres
  getChambreById(id: number): Observable<Chambre> {
    return this.http.get<Chambre>(`${this.baseUrl}/chambres/${id}`);
  }

  getChambres(): Observable<Chambre[]> {
    return this.http.get<Chambre[]>(`${this.baseUrl}/chambres`);
  }

  getChambresByStatut(statut: string): Observable<Chambre[]> {
    return this.http.get<Chambre[]>(`${this.baseUrl}/chambres/statut/${statut}`);
  }

  getChambresByBloc(bloc: string): Observable<Chambre[]> {
    return this.http.get<Chambre[]>(`${this.baseUrl}/chambres/localisation?bloc=${bloc}`);
  }

  searchChambreByNumero(numero: string): Observable<Chambre> {
    return this.http.get<Chambre>(`${this.baseUrl}/chambres/search?numero=${numero}`);
  }


  // 🔸 Ajouter une chambre
  createChambre(chambre: Chambre): Observable<number | string> {
    return this.http.post<number | string>(`${this.baseUrl}/chambres`, chambre, {
      responseType: 'text' as 'json'
    });
  }

// 🔸 Modifier une chambre
  updateChambre(id: number, chambre: Chambre): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/chambres/${id}`, chambre, {
      responseType: 'text' as 'json'
    });
  }

// 🔸 Supprimer une chambre
  deleteChambre(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/chambres/${id}`);
  }
// 🔸 Ajouter un lit
  createLit(lit: Lit): Observable<number | string> {
    return this.http.post<number | string>(`${this.baseUrl}/lits`, lit , {
      responseType: 'text' as 'json'
    });
  }

// 🔸 Modifier un lit
  updateLit(id: number, lit: Lit): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/lits/${id}`, lit, {
      responseType: 'text' as 'json'
    });
  }

// 🔸 Supprimer un lit
  deleteLit(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/lits/${id}`);
  }


  getAll(): Observable<Materiel[]> {
    return this.http.get<Materiel[]>(`${this.base2Url}`);
  }

  getById(id: number): Observable<Materiel> {
    return this.http.get<Materiel>(`${this.base2Url}/${id}`);
  }

  create(materiel: Materiel): Observable<number> {
    return this.http.post<number>(`${this.base2Url}`, materiel , {
      responseType: 'text' as 'json'
    });
  }

  update(id: number, materiel: Materiel): Observable<any> {
    return this.http.put(`${this.base2Url}/${id}`, materiel , {
      responseType: 'text' as 'json'
    });
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.base2Url}/${id}`);
  }

}
