import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {EquipementsService} from '../../services/equipements.service';
import {Chambre} from '../../models';

@Component({
  selector: 'app-chambre-form',
  standalone: false,
  templateUrl: './chambre-form.component.html',
  styleUrl: './chambre-form.component.css'
})

export class ChambreFormComponent implements OnInit {
  chambreForm!: FormGroup;
  isEditMode = false;
  chambres: Chambre[] = [];

  constructor(
    private fb: FormBuilder,
    private service: EquipementsService,
    public dialogRef: MatDialogRef<ChambreFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { chambre?: Chambre }
  ) {}

  ngOnInit(): void {
    this.isEditMode = !!this.data.chambre;
    this.chambreForm = this.fb.group({
      numero: [this.data.chambre?.numero || '', Validators.required],
      bloc: [this.data.chambre?.bloc || '', Validators.required],
      statut: [this.data.chambre?.statut || 'Libre', Validators.required],
      capacite: [this.data.chambre?.capacite || 1, [Validators.required, Validators.min(1)]],
      dateDerniereMaintenance: [this.data.chambre?.dateDerniereMaintenance || ""],
      equipements: [this.data.chambre?.equipements || '']
    });
  }

  chargerChambres(): void {
    this.service.getChambres().subscribe((data) => {
      this.chambres = data;
    });
  }

  onSubmit(): void {
    if (this.chambreForm.invalid) return;

    const formValue = this.chambreForm.value;


    if (!formValue.dateDerniereMaintenance || formValue.dateDerniereMaintenance.trim() === '') {
      formValue.dateDerniereMaintenance = null;
    }

    const chambre: Chambre = formValue;

    if (this.isEditMode && this.data.chambre?.id) {
      this.service.updateChambre(this.data.chambre.id, chambre).subscribe(() => this.dialogRef.close(true));
    } else {
      this.service.createChambre(chambre).subscribe(() => this.dialogRef.close(true));
    }
  }


}
