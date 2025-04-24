import { Component, OnInit, ViewChild } from '@angular/core';

import { RendezVousService } from '../../services/rendez-vous.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import {RendezVous} from '../../models';
import {PatientService} from '../../services/patient-service.service';
import {PersonnelServiceService} from '../../services/personnel-service.service';

@Component({
  selector: 'app-rendez-vous',
  templateUrl: './rendez-vous.component.html',
  styleUrls: ['./rendez-vous.component.css'],
  standalone:false
})
export class RendezVousComponent implements OnInit {
  displayedColumns: string[] = ['id', 'patientId', 'personnelId', 'dateHeure', 'motif', 'statut', 'actions'];
  dataSource = new MatTableDataSource<RendezVous>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  patientMap = new Map<number, string>();
  personnelMap = new Map<number, string>()

  constructor(
    private rendezVousService: RendezVousService,
    private dialog: MatDialog,
    private patientService: PatientService,
    private personnelService: PersonnelServiceService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadRendezVous();
  }

  loadRendezVous(): void {
    this.rendezVousService.getAll().subscribe({
      next: (data) => {
        this.dataSource = new MatTableDataSource<RendezVous>(data);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;

        // Chargement des noms associés
        data.forEach(rv => {
          if (!this.patientMap.has(rv.patientId)) {
            this.patientService.getById(rv.patientId).subscribe(p => {
              this.patientMap.set(rv.patientId, `${p.nom} ${p.prenom}`);
            });
          }
          if (!this.personnelMap.has(rv.personnelId)) {
            this.personnelService.getPersonnelById(rv.personnelId).subscribe(p => {
              this.personnelMap.set(rv.personnelId, `${p.nom} ${p.prenom}`);
            });
          }
        });
      },
      error: (err) => console.error('Erreur chargement rendez-vous', err)
    });
  }


  deleteRendezVous(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Supprimer un rendez-vous',
        message: 'Êtes-vous sûr de vouloir supprimer ce rendez-vous ?',
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.rendezVousService.delete(id).subscribe(() => {
          this.loadRendezVous();
          this.snackBar.open('Rendez-vous supprimé', 'Fermer', {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
          });
        });
      }
    });
  }

  ajouterRendezVous(): void {
    this.router.navigate(['/add-rendez-vous']);
  }

  editRendezVous(id: number): void {
    this.router.navigate(['/edit-rendez-vous', id]);
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
}
