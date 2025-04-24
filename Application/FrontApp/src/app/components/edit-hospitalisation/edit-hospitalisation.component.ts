import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientService } from '../../services/patient-service.service';
import { PersonnelServiceService } from '../../services/personnel-service.service';
import { EquipementsService } from '../../services/equipements.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Location } from '@angular/common';
import { Hospitalisation, Personnel, Lit } from '../../models';

@Component({
  selector: 'app-edit-hospitalisation',
  templateUrl: './edit-hospitalisation.component.html',
  styleUrls: ['./edit-hospitalisation.component.css'],
  standalone: false
})
export class EditHospitalisationComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  hospitalisationId?: number;
  patientId?: number;
  medecins: Personnel[] = [];
  lits: Lit[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService,
    private personnelService: PersonnelServiceService,
    private equipementService: EquipementsService,
    private snackBar: MatSnackBar,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      dateDebut: ['', Validators.required],
      dateFin: [''],
      motif: ['', [Validators.maxLength(500)]],
      notes: [''],
      personnelResponsableId: ['', Validators.required],
      litId: ['', Validators.required]
    });

    this.loadLitsDisponibles();
    this.loadMedecins();


    const currentUrl = this.router.url;

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');

      if (currentUrl.includes('/editHospitalisation')) {
        this.isEditMode = true;
        this.hospitalisationId = +id!;
        this.loadHospitalisation(this.hospitalisationId);
      } else if (currentUrl.includes('/addHospitalisation')) {
        this.isEditMode = false;
        this.patientId = +id!;
      } else {
        console.error("URL invalide.");
        this.goBack();
      }
    });
  }


  loadMedecins(): void {
    this.personnelService.getMedecins().subscribe({
      next: (data) => this.medecins = data
    });
  }

  loadLitsDisponibles(): void {
    this.equipementService.getLitsByStatut('LIBRE').subscribe({
      next: lits => this.lits = lits,
      error: err => console.error('Erreur chargement lits', err)
    });
  }


  loadHospitalisation(id: number): void {
    this.patientService.getHospitalisationById(id).subscribe({
      next: (data) => {
        const patchForm = () => {
          const litExist = this.lits.find(l => l.id === data.litId);
          if (!litExist) {
            this.equipementService.getLitById(data.litId).subscribe({
              next: lit => {
                this.lits.push(lit);
                applyPatch();
              },
              error: () => applyPatch()
            });
          } else {
            applyPatch();
          }
        };

        const applyPatch = () => {
          this.form.patchValue({
            ...data,
            dateDebut: new Date(data.dateDebut),
            dateFin: data.dateFin ? new Date(data.dateFin) : '',
            personnelResponsableId: data.personnelResponsableId,
            litId: data.litId
          });
        };

        if (!this.lits.length) {
          const checkInterval = setInterval(() => {
            if (this.lits.length) {
              clearInterval(checkInterval);
              patchForm();
            }
          }, 100);
        } else {
          patchForm();
        }

        this.patientId = data.patientId;
      },
      error: () => this.goBack()
    });
  }

  formatDate(date: Date): string {
    const iso = new Date(date).toISOString();
    return iso.split('.')[0];
  }

  onSubmit(): void {
    if (this.form.invalid || !this.patientId) return;

    const formData = this.form.value;

    const hospitalisation: Hospitalisation = {
      ...formData,
      dateDebut: this.formatDate(formData.dateDebut),
      dateFin: formData.dateFin ? this.formatDate(formData.dateFin) : null,
      patientId: this.patientId,
      personnelResponsableId: formData.personnelResponsableId,
      litId: formData.litId
    };

    const request = this.isEditMode
      ? this.patientService.updateHospitalisation(this.hospitalisationId!, hospitalisation)
      : this.patientService.createHospitalisation(hospitalisation);

    request.subscribe({
      next: () => {
        this.snackBar.open(
          this.isEditMode
            ? 'Hospitalisation mise à jour avec succès'
            : 'Hospitalisation ajoutée avec succès',
          'Fermer',
          { duration: 3000 }
        );
        this.router.navigate(['/hospitaliser', this.patientId]);
      },
      error: () => {
        this.snackBar.open('Erreur lors de la sauvegarde', 'Fermer', { duration: 3000 });
      }
    });
  }

  goBack(): void {
    this.location.back();
  }
}
