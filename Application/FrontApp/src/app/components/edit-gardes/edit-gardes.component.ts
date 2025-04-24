import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Garde, Personnel} from '../../models';
import {PersonnelServiceService} from '../../services/personnel-service.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-edit-gardes',
  standalone: false,
  templateUrl: './edit-gardes.component.html',
  styleUrl: './edit-gardes.component.css'
})
export class EditGardesComponent implements OnInit {
  gardeForm!: FormGroup;
  isEditMode = false;
  gardeId!: number;
  personnels: Personnel[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    protected router: Router,
    private personnelService: PersonnelServiceService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadPersonnels();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.gardeId = +idParam;
      this.loadGarde(this.gardeId);
    }
  }

  initForm(): void {
    this.gardeForm = this.fb.group({
      dateDebutPeriode: ['', Validators.required],
      dateFinPeriode: ['', Validators.required],
      heureDebut: ['', Validators.required],
      heureFin: ['', Validators.required],
      personnelId: [null, Validators.required]
    });
  }

  loadGarde(id: number): void {
    this.personnelService.getGardeById(id).subscribe({
      next: (garde) => {
        this.gardeForm.patchValue({
          dateDebutPeriode: garde.dateDebutPeriode,
          dateFinPeriode: garde.dateFinPeriode,
          heureDebut: garde.heureDebut,
          heureFin: garde.heureFin,
          personnelId: garde.personnelId
        });
      },
      error: (err) => console.error('Erreur chargement garde', err)
    });
  }

  loadPersonnels(): void {
    this.personnelService.getAllPersonnel().subscribe({
      next: (data) => (this.personnels = data),
      error: (err) => console.error('Erreur chargement personnels', err)
    });
  }

  onSubmit(): void {
    if (this.gardeForm.invalid) return;

    const garde: Garde = {
      dateDebutPeriode: this.gardeForm.value.dateDebutPeriode,
      dateFinPeriode: this.gardeForm.value.dateFinPeriode,
      heureDebut: this.gardeForm.value.heureDebut,
      heureFin: this.gardeForm.value.heureFin,
      personnelId: this.gardeForm.value.personnelId
    };

    if (this.isEditMode) {
      this.personnelService.modifierGarde(this.gardeId, garde).subscribe({
        next: () => {
          this.snackBar.open('Garde modifiée avec succès !', 'Fermer', {
            duration: 3000,
          });
          this.router.navigate(['/gardes']);
        },
        error: (err) => {
          console.error('Erreur modification garde', err);
          this.snackBar.open('Erreur lors de la modification.', 'Fermer', {
            duration: 3000,
          });
        }
      });
    } else {
      this.personnelService.planifierGarde(garde).subscribe({
        next: () => {
          this.snackBar.open('Garde ajoutée avec succès !', 'Fermer', {
            duration: 3000,
          });
          this.router.navigate(['/gardes']);
        },
        error: (err) => {
          console.error('Erreur ajout garde', err);
          this.snackBar.open('Erreur lors de l\'ajout.', 'Fermer', {
            duration: 3000,
          });
        }
      });
    }
  }

}
