import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';


import { Materiel } from '../../models';
import {EquipementsService} from '../../services/equipements.service'; // À adapter selon ton modèle

@Component({
  selector: 'app-edit-materiels',
  templateUrl: './edit-materiels.component.html',
  styleUrls: ['./edit-materiels.component.css'],
  standalone: false
})
export class EditMaterielsComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  materielId?: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar,
    private materielService: EquipementsService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nom: ['', Validators.required],
      typeMateriel: ['', Validators.required],
      quantite: [1, [Validators.required, Validators.min(1)]],
      statut: ['', Validators.required],
      dateAchat: ['', Validators.required],
      fournisseur: ['', Validators.required]
    });

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.materielId = +id;
        this.loadMateriel(this.materielId);
      }
    });
  }

  loadMateriel(id: number): void {
    this.materielService.getById(id).subscribe(mat => {
      const date = new Date(mat.dateAchat);
      const localDate = date.toISOString().slice(0, 10); // YYYY-MM-DD
      this.form.patchValue({
        ...mat,
        dateAchat: localDate
      });
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    const formValue = {
      ...this.form.value,
      dateAchat: this.form.value.dateAchat + 'T00:00:00' // format ISO pour backend
    };

    const request = this.isEditMode
      ? this.materielService.update(this.materielId!, formValue)
      : this.materielService.create(formValue);

    request.subscribe(() => {
      this.snackBar.open(
        `Matériel ${this.isEditMode ? 'modifié' : 'ajouté'} avec succès !`,
        'Fermer',
        { duration: 3000, horizontalPosition: 'right', verticalPosition: 'top' }
      );
      this.router.navigate(['/materiels']);
    });
  }
}
