import { Component, OnInit } from '@angular/core';
import {Chambre, Hospitalisation, Lit} from '../../models';
import { Patient, PatientService } from '../../services/patient-service.service';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { PersonnelServiceService } from '../../services/personnel-service.service';
import {EquipementsService} from '../../services/equipements.service';

@Component({
  selector: 'app-hospitalisation',
  standalone: false,
  templateUrl: './hospitalisation.component.html',
  styleUrls: ['./hospitalisation.component.css']
})
export class HospitalisationComponent implements OnInit {
  hospitalisations: Hospitalisation[] = [];
  patientId!: number;
  isLoading = false;
  patient: Patient | undefined;
  medecinsMap: { [key: number]: string } = {};
  litsMap: { [key: number]: Lit } = {};
  chambresMap: { [key: number]: Chambre } = {};

  constructor(
    private patientService: PatientService,
    private personnelService: PersonnelServiceService,
    private route: ActivatedRoute,
    private location: Location,
    private dialog: MatDialog,
    private equipementService: EquipementsService
  ) {}

  ngOnInit() {
    this.patientId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadHospitalisations();
    this.loadPatientInfo();
  }

  getDateRange(hosp: Hospitalisation): string {
    const debut = new Date(hosp.dateDebut).toLocaleString();
    const fin = hosp.dateFin ? new Date(hosp.dateFin).toLocaleString() : 'En cours';
    return `${debut} - ${fin}`;
  }

  loadHospitalisations(): void {
    this.isLoading = true;
    this.patientService.getHospitalisationsByPatientId(this.patientId).subscribe({
      next: (data) => {
        this.hospitalisations = data;
        this.isLoading = false;

        data.forEach(hospitalisation => {
          const medecinId = hospitalisation.personnelResponsableId;
          if (medecinId && !this.medecinsMap[medecinId]) {
            this.personnelService.getPersonnelById(medecinId).subscribe({
              next: (medecin) => {
                this.medecinsMap[medecinId] = `${medecin.prenom} ${medecin.nom}`;
              },
              error: (err) => console.error(`Erreur chargement médecin ${medecinId}`, err)
            });
          }

          const litId = hospitalisation.litId;
          if (litId && !this.litsMap[litId]) {
            this.equipementService.getLitById(litId).subscribe({
              next: (lit) => {
                this.litsMap[litId] = lit;
                if (lit.chambreId && !this.chambresMap[lit.chambreId]) {
                  this.equipementService.getChambreById(lit.chambreId).subscribe({
                    next: (chambre) => {
                      this.chambresMap[lit.chambreId] = chambre;
                    },
                    error: (err) => console.error(`Erreur chargement chambre ${lit.chambreId}`, err)
                  });
                }
              },
              error: (err) => console.error(`Erreur chargement lit ${litId}`, err)
            });
          }
        });
      },
      error: (err) => {
        console.error('Erreur lors du chargement des hospitalisations', err);
        this.isLoading = false;
      }
    });
  }

  loadPatientInfo(): void {
    this.patientService.getById(this.patientId).subscribe({
      next: (data) => {
        this.patient = data;
      },
      error: (err) => console.error('Erreur chargement patient', err)
    });
  }

  deleteHospitalisation(hospitalisationId: any): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Supprimer une hospitalisation',
        message: 'Voulez-vous vraiment supprimer cette hospitalisation ?',
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.patientService.deleteHospitalisation(hospitalisationId).subscribe({
          next: () => {
            this.hospitalisations = this.hospitalisations.filter(h => h.id !== hospitalisationId);
          },
          error: (err) => console.error('Erreur suppression hospitalisation', err)
        });
      }
    });
  }

  getMedecinNom(id: number | undefined): string {
    return id !== undefined && this.medecinsMap[id] ? this.medecinsMap[id] : 'Inconnu';
  }

  getLitNumero(id: number): string {
    return this.litsMap[id]?.numero || 'Lit inconnu';
  }

  getChambreNumeroByLitId(litId: number): string {
    const chambreId = this.litsMap[litId]?.chambreId;
    return chambreId && this.chambresMap[chambreId] ? this.chambresMap[chambreId].numero : 'Chambre inconnue';
  }

  goBack(): void {
    this.location.back();
  }
}
