import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Patient, PatientService } from '../../services/patient-service.service';
import { DossierMedical } from '../../models';

@Component({
  selector: 'app-dossier',
  templateUrl: './dossier.component.html',
  styleUrls: ['./dossier.component.css'],
  standalone: false
})
export class DossierComponent implements OnInit {
  patientId: number;
  currentDossierId: number | null = null;
  patient: Patient | null = null;
  dossiers: DossierMedical[] = [];
  isLoading = false;

  constructor(
    private route: ActivatedRoute,
    private dossierService: PatientService,
    private router: Router,


  ) {
    this.patientId = Number(this.route.snapshot.paramMap.get('id'));
  }

  ngOnInit(): void {
    this.loadPatientData();
  }
  dossier: DossierMedical | null = null;
  loadPatientData(): void {
    this.isLoading = true;
    this.dossierService.getPatientWithDossiers(this.patientId).subscribe({
      next: (data) => {
        this.patient = data.patient;
        this.dossiers = data.dossiers || [];
        this.dossier = this.dossiers[0] || null; // <-- ajoute ça
        this.currentDossierId = this.dossier?.id || null;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des données du dossier médical', err);
        this.isLoading = false;
      }
    });

  }

  modifierDossier(idDossier: number | null): void {
    if (!idDossier) {
      console.warn('Aucun ID de dossier fourni');
      return;
    }
    this.router.navigate(['/edit-dossier', idDossier]);
  }

  calculateAge(dateNaissance?: string): number {
    if (!dateNaissance) return 0;

    try {
      const birthDate = new Date(dateNaissance);
      const today = new Date();
      let age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();

      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }

      return age;
    } catch (e) {
      console.error('Erreur de calcul de l\'âge', e);
      return 0;
    }
  }

  Consultation(id: number | undefined) {
    if (!id) {
      console.warn('Aucun ID de dossier fourni');
      return;
    }
    this.router.navigate(['/consulter', id]);

  }

  hospitaliser(id: number | undefined) {
    if (!id) {
      console.warn('Aucun ID de dossier fourni');
      return;
    }

    this.router.navigate(['/hospital', id]);
  }

}
