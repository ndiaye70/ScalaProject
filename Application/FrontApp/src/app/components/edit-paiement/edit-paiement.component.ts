import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PaiementService } from '../../services/paiement.service';
import { PatientService, Patient } from '../../services/patient-service.service';
import { Paiement } from '../../models';
import { MatSnackBar } from '@angular/material/snack-bar';
import {take} from 'rxjs';

@Component({
  selector: 'app-edit-paiement',
  templateUrl: './edit-paiement.component.html',
  styleUrls: ['./edit-paiement.component.css'],
  standalone: false
})
export class EditPaiementComponent implements OnInit {
  paiementForm!: FormGroup;
  isEditMode = false;
  paiementId!: number;
  patients: Patient[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private paiementService: PaiementService,
    private patientService: PatientService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadPatients();

    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.paiementId = +params['id'];
        this.loadPaiement();
      }
    });
  }

  initForm(): void {
    this.paiementForm = this.fb.group({
      patientId: [null, Validators.required],
      montant: [null, [Validators.required, Validators.min(0)]],
      mode: ['', Validators.required],
      datePaiement: ['', Validators.required],
      reference: ['']
    });
  }

  loadPatients(): void {
    this.patientService.getAll().subscribe(patients => this.patients = patients);
  }

  loadPaiement(): void {
    this.paiementService.getById(this.paiementId).subscribe(paiement => {
      const formattedDate = paiement.datePaiement?.slice(0, 19);
      this.paiementForm.patchValue({
        ...paiement,
        datePaiement: formattedDate
      });
    });
  }

  onSubmit(): void {
    if (this.paiementForm.invalid) return;

    const formValues = this.paiementForm.value;
    const date = new Date(formValues.datePaiement);
    const paiement: Paiement = {
      ...formValues,
      datePaiement: date.toISOString().split('.')[0]
    };

    const operation$ = this.isEditMode
      ? this.paiementService.update(this.paiementId, paiement)
      : this.paiementService.create(paiement);

    operation$.pipe(take(1)).subscribe({
      next: () => {
        this.snackBar.open(
          `Paiement ${this.isEditMode ? 'modifié' : 'ajouté'} avec succès`,
          'Fermer',
          {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
          }
        );
        this.router.navigate(['/paiements']);
      },
      error: (err) => {
        console.error(`Erreur lors de ${this.isEditMode ? 'la modification' : 'l\'ajout'} :`, err);
        this.snackBar.open(
          `Erreur lors de ${this.isEditMode ? 'la modification' : 'l\'ajout'}`,
          'Fermer',
          { duration: 3000 }
        );
      }
    });
  }
  annuler(): void {
    this.router.navigate(['/paiements']);
  }
}
