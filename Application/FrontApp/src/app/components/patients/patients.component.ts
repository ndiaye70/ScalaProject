import { Component, OnInit, ViewChild } from '@angular/core';
import { Patient, PatientService } from '../../services/patient-service.service';
import { MatTableDataSource } from '@angular/material/table';
import {Router} from '@angular/router';
import {ConfirmDialogComponent} from '../confirm-dialog/confirm-dialog.component';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';


@Component({
  selector: 'app-patients',
  templateUrl: './patients.component.html',
  standalone:false,
  styleUrls: ['./patients.component.css']
})
export class PatientsComponent implements OnInit {
  displayedColumns: string[] = ['nom', 'prenom', 'telephone','numeroAssurance','codePatient', 'actions'];
  dataSource = new MatTableDataSource<Patient>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private patientService: PatientService,private dialog: MatDialog,
              private snackBar: MatSnackBar,private router: Router) { }

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.patientService.getAll().subscribe({
      next: (data) => {
        this.dataSource = new MatTableDataSource<Patient>(data);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (err) => console.error('Erreur chargement patients', err)
    });
  }

  deletePatient(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Supprimer un patient',
        message: 'Cette action est irréversible. Supprimer ce patient ?',
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    });


    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.patientService.delete(id).subscribe(() => {
          this.loadPatients();
          this.snackBar.open('Patient supprimé avec succès !', 'Fermer', {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
          });
        });
      }
    });
  }


  ouvrirDossier(id: number): void {
    this.router.navigate(['/dossier', id]);
  }

  ajouterPatient(): void {
    this.router.navigate(['/patients/add']);
  }

  editPatient(id: number): void {
    this.router.navigate(['/patients/edit', id]);
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
}
