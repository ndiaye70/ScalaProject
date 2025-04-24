import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

import { RendezVousService } from '../../services/rendez-vous.service';
import {Patient, PatientService} from '../../services/patient-service.service';


import {  Personnel,} from '../../models';
import {PersonnelServiceService} from '../../services/personnel-service.service';

@Component({
  selector: 'app-edit-rendez-vous',
  templateUrl: './edit-rendez-vous.component.html',
  styleUrls: ['./edit-rendez-vous.component.css'],
  standalone: false
})
export class EditRendezVousComponent implements OnInit {
  form!: FormGroup;
  patients: Patient[] = [];
  personnels: Personnel[] = [];
  isEditMode = false;
  rendezVousId?: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar,
    private rendezVousService: RendezVousService,
    private patientService: PatientService,
    private personnelService: PersonnelServiceService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      patientId: [null, Validators.required],
      personnelId: [null, Validators.required],
      dateHeure: [null, Validators.required],
      motif: ['', Validators.required],
      statut: ['', Validators.required]
    });

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.rendezVousId = +id;
        this.loadRendezVous(this.rendezVousId);
      }
    });

    this.loadPatients();
    this.loadPersonnels();
  }

  loadRendezVous(id: number): void {
    this.rendezVousService.getById(id).subscribe(rv => {
      // Convertit la date ISO complète en version compatible input type="datetime-local"
      const date = new Date(rv.dateHeure);
      const localDate = date.toISOString().slice(0, 16); // "YYYY-MM-DDTHH:mm"
      this.form.patchValue({
        ...rv,
        dateHeure: localDate
      });
    });
  }


  loadPatients(): void {
    this.patientService.getAll().subscribe(patients => {
      this.patients = patients;
    });
  }

  loadPersonnels(): void {
    this.personnelService.getAllPersonnel().subscribe(personnels => {
      this.personnels = personnels;
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    let formValue = { ...this.form.value };

    // Ajoute ":00" si besoin à la fin pour avoir les secondes
    if (formValue.dateHeure.length === 16) {
      formValue.dateHeure += ':00';
    }

    const request = this.isEditMode
      ? this.rendezVousService.update(this.rendezVousId!, formValue)
      : this.rendezVousService.create(formValue);

    request.subscribe(() => {
      this.snackBar.open(`Rendez-vous ${this.isEditMode ? 'modifié' : 'ajouté'} avec succès !`, 'Fermer', {
        duration: 3000,
        horizontalPosition: 'right',
        verticalPosition: 'top'
      });
      this.router.navigate(['/rendez-vous']);
    });
  }

}
