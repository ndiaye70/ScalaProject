import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Location } from '@angular/common';
import { PersonnelServiceService } from '../../services/personnel-service.service';
import {Personnel} from '../../models';


@Component({
  selector: 'app-edit-personnel',
  templateUrl: './edit-personnel.component.html',
  styleUrls: ['./edit-personnel.component.css'],
  standalone: false
})
export class EditPersonnelComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  personnelId?: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private personnelService: PersonnelServiceService,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      specialite: ['', Validators.required],
      typePersonnel: ['', Validators.required],
      telephone: ['', [Validators.required, Validators.pattern(/^(\+?\d{9,15})$/)]],
      horaires: ['', Validators.required]
    });

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');

      if (id) {
        this.isEditMode = true;
        this.personnelId = +id;
        this.loadPersonnel(this.personnelId);
      }
    });
  }

  loadPersonnel(id: number): void {
    this.personnelService.getPersonnelById(id).subscribe({
      next: (data) => {
        this.form.patchValue(data);
      },
      error: (err) => {
        console.error('Erreur chargement personnel', err);
        this.goBack();
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    const formData = this.form.value;

    const personnel: Personnel = {
      ...formData,
      ...(this.isEditMode ? { id: this.personnelId } : {})
    };

    const request = this.isEditMode
      ? this.personnelService.updatePersonnel(this.personnelId!, personnel)
      : this.personnelService.createPersonnel(personnel);

    request.subscribe({
      next: () => this.router.navigate(['/personnels']),
      error: (err) => console.error('Erreur lors de la sauvegarde', err)
    });
  }

  goBack(): void {
    this.location.back();
  }
}
