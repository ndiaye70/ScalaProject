import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Consultation, DossierMedical, Hospitalisation} from '../models';

export interface Patient {
  id?: number;
  nom: string;
  prenom: string;
  dateNaissance: string;
  sexe: string;
  telephone: string;
  adresse: string;
  numeroAssurance?: string;
  codePatient: string;
}

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private baseUrl = 'http://localhost:8081/patients';
  private dossierUrl = 'http://localhost:8081/dossiers';
  private consultationUrl = 'http://localhost:8081/consultations';
  private hospitalisationUrl = 'http://localhost:8081/hospitalisations';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Patient[]> {
    return this.http.get<Patient[]>(this.baseUrl);
  }

  getById(id: number): Observable<Patient> {
    return this.http.get<Patient>(`${this.baseUrl}/${id}`);
  }

  create(patient: Patient): Observable<any> {
    return this.http.post(this.baseUrl, patient, {
      responseType: 'text' as 'json' //
    });
  }

  update(id: number, patient: Patient): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, patient, {
      responseType: 'text' as 'json'
    });
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`, {
      responseType: 'text' as 'json'
    });
  }

  getDossierByPatientId(patientId: number): Observable<DossierMedical[]> {
    return this.http.get<DossierMedical[]>(`${this.dossierUrl}?patientId=${patientId}`);
  }

  /** Mettre à jour un dossier médical */
  updateDossier(id: number, dossier: DossierMedical): Observable<any> {
    return this.http.put(`${this.dossierUrl}/${id}`, dossier, {
      responseType: 'text' as 'json'
    });
  }

  // dossier-medical.service.ts

  getPatientWithDossiers(patientId: number): Observable<{ patient: Patient, dossiers: DossierMedical[] }> {
    return this.http.get<{ patient: Patient, dossiers: DossierMedical[] }>(`http://localhost:8081/patients/${patientId}/with-dossiers`);
  }
  getDossierById(id: number): Observable<DossierMedical> {
    return this.http.get<DossierMedical>(`${this.dossierUrl}/${id}`);
  }


  //consultation

  getConsultationById(id: number): Observable<Consultation> {
    return this.http.get<Consultation>(`${this.consultationUrl}/${id}`);
  }

  getConsultationsByPatientId(patientId: number): Observable<Consultation[]> {
    return this.http.get<Consultation[]>(`${this.consultationUrl}/patient/${patientId}`);
  }


  deleteConsultation(consultationId: number | undefined) {
    return this.http.delete(`${this.consultationUrl}/${consultationId}`, {
      responseType: 'text' as 'json'
    });

  }

  updateConsultation(consultation: Consultation) {
    return this.http.put(`${this.consultationUrl}/${consultation.id}`, consultation, {
      responseType: 'text' as 'json'
    });


  }

  addConsultation(consultation: Consultation) {
    return this.http.post(this.consultationUrl, consultation, {
      responseType: 'text' as 'json' //
    });
  }

  /** Récupérer toutes les hospitalisations */
  getAllHospitalisations(): Observable<Hospitalisation[]> {
    return this.http.get<Hospitalisation[]>(this.hospitalisationUrl);
  }

  /** Récupérer une hospitalisation par son ID */
  getHospitalisationById(id: number): Observable<Hospitalisation> {
    return this.http.get<Hospitalisation>(`${this.hospitalisationUrl}/${id}`);
  }

  /** Créer une nouvelle hospitalisation */
  createHospitalisation(hospitalisation: Hospitalisation): Observable<any> {
    return this.http.post(this.hospitalisationUrl, hospitalisation, {
      responseType: 'text' as 'json' // réponse en texte
    });
  }

  /** Mettre à jour une hospitalisation existante */
  updateHospitalisation(id: number, hospitalisation: Hospitalisation): Observable<any> {
    return this.http.put(`${this.hospitalisationUrl}/${id}`, hospitalisation, {
      responseType: 'text' as 'json'
    });
  }

  /** Supprimer une hospitalisation */
  deleteHospitalisation(id: number): Observable<any> {
    return this.http.delete(`${this.hospitalisationUrl}/${id}`, {
      responseType: 'text' as 'json'
    });
  }

  /** Récupérer toutes les hospitalisations d'un patient */
  getHospitalisationsByPatientId(patientId: number): Observable<Hospitalisation[]> {
    return this.http.get<Hospitalisation[]>(`${this.hospitalisationUrl}/patient/${patientId}`);
  }

  /** Récupérer toutes les hospitalisations liées à un lit */
  getHospitalisationsByLitId(litId: number): Observable<Hospitalisation[]> {
    return this.http.get<Hospitalisation[]>(`${this.hospitalisationUrl}/lit/${litId}`);
  }

  /** Récupérer toutes les hospitalisations ayant un statut spécifique */
  getHospitalisationsByStatut(status: string): Observable<Hospitalisation[]> {
    return this.http.get<Hospitalisation[]>(`${this.hospitalisationUrl}/statut/${status}`);
  }




}
