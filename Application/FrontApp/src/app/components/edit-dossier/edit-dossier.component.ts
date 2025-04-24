import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientService,  } from '../../services/patient-service.service';
import {DossierMedical} from '../../models';

@Component({
  selector: 'app-edit-dossier',
  templateUrl: './edit-dossier.component.html',
  styleUrls: ['./edit-dossier.component.css'],
  standalone:false
})
export class EditDossierComponent implements OnInit {
  dossierId!: number;
  dossier: DossierMedical = {
    id: 0,
    patientId: 0,
    antecedents: '',
    allergies: '',
    traitementsEnCours: '',
    notes: ''
  };
  loading: boolean = true;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService
  ) {}

  ngOnInit(): void {
    this.dossierId = Number(this.route.snapshot.paramMap.get('id'));

    this.patientService.getDossierById(this.dossierId).subscribe({
      next: (dossier) => {
        this.dossier = dossier;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement du dossier.';
        this.loading = false;
      }
    });
  }

  updateDossier(): void {
    if (this.dossier && this.dossier.id) {
      this.patientService.updateDossier(this.dossier.id, this.dossier).subscribe({
        next: () => this.router.navigate(['/dossier', this.dossier.patientId]),
        error: () => this.errorMessage = 'Erreur lors de la mise à jour.'
      });
    }
  }
}
