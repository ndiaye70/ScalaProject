import { Component, OnInit } from '@angular/core';
import {Patient, PatientService} from '../../services/patient-service.service';
import { ActivatedRoute } from '@angular/router';
import {Consultation} from '../../models';
import { Location } from '@angular/common';
import {ConfirmDialogComponent} from '../confirm-dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {PersonnelServiceService} from '../../services/personnel-service.service';


@Component({
  selector: 'app-consultation',
  standalone: false,
  templateUrl: './consultation.component.html',
  styleUrls: ['./consultation.component.css']
})
export class ConsultationComponent implements OnInit {
  consultations: Consultation[] = [];
  patientId!: number;
  isLoading = false;
  patient: Patient | undefined;
  medecinsMap: { [key: number]: string } = {};


  constructor(
    private patientService: PatientService,
    private personnelService:PersonnelServiceService,
    private route: ActivatedRoute,
    private location: Location,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.patientId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadConsultations();
    this.loadPatientInfo();
  }

  loadConsultations(): void {
    this.isLoading = true;
    this.patientService.getConsultationsByPatientId(this.patientId).subscribe({
      next: (data) => {
        this.consultations = data;
        this.isLoading = false;

        // Charger les médecins correspondants
        data.forEach(consultation => {
          const medecinId = consultation.personnelId;
          if (medecinId && !this.medecinsMap[medecinId]) {
            this.personnelService.getPersonnelById(medecinId).subscribe({
              next: (medecin) => {
                this.medecinsMap[medecinId] = `${medecin.prenom} ${medecin.nom}`;
              },
              error: (err) => {
                console.error(`Erreur chargement médecin ${medecinId}`, err);
              }
            });
          }
        });
      },
      error: (err) => {
        console.error('Erreur lors du chargement des consultations', err);
        this.isLoading = false;
      }
    });
  }

  loadPatientInfo(): void {
    this.patientService.getById(this.patientId).subscribe({
      next: (data) => {
        this.patient = data
          //this.do
      },
      error: (err) => console.error('Erreur chargement patient', err)
    });
  }



  deleteConsultation(consultationId: number | undefined): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Supprimer une consultation',
        message: 'Voulez-vous vraiment supprimer cette consultation ?',
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    });


    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.patientService.deleteConsultation(consultationId).subscribe({
          next: () => {
            this.consultations = this.consultations.filter(c => c.id !== consultationId);
          },
          error: (err) => console.error('Erreur suppression consultation', err)
        });
      }
    });
  }

  goBack(): void {
    this.location.back();
  }

}
