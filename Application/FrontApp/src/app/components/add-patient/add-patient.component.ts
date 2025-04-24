import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PatientService, Patient } from '../../services/patient-service.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-patient',
  templateUrl: './add-patient.component.html',
  styleUrls: ['./add-patient.component.css'],
  standalone: false,
})
export class AddPatientComponent implements OnInit {
  formPatient!: FormGroup;
  isEditMode = false;
  patientId?: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.formPatient = this.fb.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      dateNaissance: ['', Validators.required],
      sexe: ['', Validators.required],
      telephone: ['', Validators.required],
      adresse: ['', Validators.required],
      numeroAssurance: [''],
      codePatient: ['', Validators.required],
    });

    this.patientId = Number(this.route.snapshot.paramMap.get('id'));
    this.isEditMode = !!this.patientId;

    if (this.isEditMode) {
      this.patientService.getById(this.patientId).subscribe({
        next: (patient) => this.formPatient.patchValue(patient),
        error: () =>
          this.snackBar.open('Erreur lors du chargement du patient', 'Fermer', {
            duration: 3000,
          }),
      });
    }
  }


  goBack(): void {
    this.router.navigate(['/patients']);
  }


  onSubmit(): void {
    if (this.formPatient.invalid) return;

    const patient: Patient = this.formPatient.value;

    if (this.isEditMode) {
      this.patientService.update(this.patientId!, patient).subscribe({
        next: (res) => {
          this.snackBar.open('Patient mis à jour avec succès', 'Fermer', {
            duration: 3000,
          });
          this.router.navigate(['/patients']);
        },
        error: () =>
          this.snackBar.open('Erreur lors de la mise à jour', 'Fermer', {
            duration: 3000,
          }),
      });
    } else {
      this.patientService.create(patient).subscribe({
        next: (res) => {
          this.snackBar.open('Patient créé avec succès', 'Fermer', {
            duration: 3000,
          });
          this.router.navigate(['/patients']);
        },
        error: () =>
          this.snackBar.open('Erreur lors de la création', 'Fermer', {
            duration: 3000,
          }),
      });
    }
  }
}
