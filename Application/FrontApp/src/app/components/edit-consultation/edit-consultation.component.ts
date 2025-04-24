import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PatientService } from '../../services/patient-service.service';
import {Consultation, Personnel} from '../../models';
import { Location } from '@angular/common';
import {map, Observable, startWith} from 'rxjs';
import {PersonnelServiceService} from '../../services/personnel-service.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-edit-consultation',
  templateUrl: './edit-consultation.component.html',
  styleUrls: ['./edit-consultation.component.css'],
  standalone: false
})
export class EditConsultationComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  consultationId?: number;
  patientId?: number;
  medecins: Personnel[] = [];
  filteredMedecins!: Observable<Personnel[]>;


  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private personnelService:PersonnelServiceService,
    private patientService: PatientService,
    private location: Location,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      dateRealisation: ['', Validators.required],
      symptomes: ['', [Validators.required, Validators.maxLength(1000)]],
      diagnostic: ['', [Validators.required, Validators.maxLength(2000)]],
      prescriptions: [''],
      notesInternes: [''],
      personnelId: ['', Validators.required] // <-- Champ médecin obligatoire
    });

    this.personnelService.getMedecins().subscribe({
      next: (data) => {
        this.medecins = data;

        this.filteredMedecins = this.form.get('personnelId')!.valueChanges.pipe(
          startWith(''),
          map(value => typeof value === 'string' ? value : this.getFullName(value)),
          map(name => name ? this.filterMedecins(name) : this.medecins.slice())
        );
      },
      error: (err) => console.error('Erreur chargement médecins', err)
    });





    this.route.paramMap.subscribe(params => {
      const idConsult = params.get('idConsultation');
      const idPatient = params.get('idPatient');

      if (idConsult) {
        this.isEditMode = true;
        this.consultationId = +idConsult;
        this.loadConsultation(this.consultationId);
      } else if (idPatient) {
        this.patientId = +idPatient;
      } else {
        console.error("Aucun ID de consultation ou de patient fourni.");
        this.goBack();
      }
    });
  }



  loadConsultation(id: number): void {
    this.patientService.getConsultationById(id).subscribe({
      next: (data) => {
        this.form.patchValue({
          ...data,
          dateRealisation: new Date(data.dateRealisation),
          personnelId: this.medecins.find(m => m.id === data.personnelId) // <-- On ajoute ici
        });
        this.patientId = data.patientId;
      },
      error: (err) => {
        console.error('Erreur chargement consultation', err);
        this.goBack();
      }
    });
  }


  // 🔧 Méthode utilitaire pour formater la date sans timezone
  private formatDate(date: Date): string {
    const iso = new Date(date).toISOString();
    return iso.split('.')[0]; // "YYYY-MM-DDTHH:mm:ss"
  }

  onSubmit(): void {
    if (this.form.invalid || !this.patientId) return;

    const selectedMedecin = this.form.value.personnelId;
    const personnelId = typeof selectedMedecin === 'object' ? selectedMedecin.id : selectedMedecin;

    const formData = this.form.value;

    const consultation: Consultation = {
      ...formData,
      dateRealisation: this.formatDate(formData.dateRealisation),
      patientId: this.patientId,
      personnelId,
      ...(this.isEditMode ? { id: this.consultationId } : {})
    };

    const request = this.isEditMode
      ? this.patientService.updateConsultation(consultation)
      : this.patientService.addConsultation(consultation);

    request.subscribe({
      next: () => {
        // Affichage du message de confirmation
        this.snackBar.open(
          this.isEditMode ? 'Consultation mise à jour avec succès' : 'Consultation créée avec succès',
          'Fermer',
          { duration: 3000 } // Message visible pendant 3 secondes
        );
        this.router.navigate(['/consulter', this.patientId]);
      },
      error: (err) => {
        console.error('Erreur lors de la sauvegarde', err);
        // Affichage d'un message d'erreur en cas de problème
        this.snackBar.open('Erreur lors de la sauvegarde', 'Fermer', { duration: 3000 });
      }
    });
  }


  goBack(): void {
    this.location.back();
  }

  private getFullName(medecin: Personnel): string {
    return `${medecin.prenom} ${medecin.nom}`;
  }

  private filterMedecins(name: string): Personnel[] {
    const filterValue = name.toLowerCase();
    return this.medecins.filter(m =>
      this.getFullName(m).toLowerCase().includes(filterValue)
    );
  }

  displayMedecin(medecin: Personnel): string {
    return medecin ? this.getFullName(medecin) : '';
  }

}
