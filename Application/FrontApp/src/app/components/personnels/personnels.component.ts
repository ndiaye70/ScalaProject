import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import {PersonnelServiceService} from '../../services/personnel-service.service';
import {Personnel} from '../../models';
import {ConfirmDialogComponent} from '../confirm-dialog/confirm-dialog.component';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatDialog} from '@angular/material/dialog';


@Component({
  selector: 'app-personnels',
  templateUrl: './personnels.component.html',
  styleUrl: './personnels.component.css',
  standalone: false
})
export class PersonnelsComponent implements OnInit {
  displayedColumns: string[] = ['id', 'nom', 'prenom', 'specialite', 'typePersonnel', 'telephone', 'actions'];
  dataSource: MatTableDataSource<Personnel> = new MatTableDataSource<Personnel>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private personnelService: PersonnelServiceService,private dialog: MatDialog,
              private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.loadPersonnels();
    this.personnelService.getAllPersonnel().subscribe({
      next: (data) => {
        this.dataSource.data = data;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (err) => console.error('Erreur de chargement du personnel', err)
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.dataSource.filter = filterValue;
  }

  ajouterPersonnel(): void {
    // Rediriger vers un formulaire ou afficher un modal
  }

  editPersonnel(id: number | undefined): void {

  }

  deletePersonnel(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Supprimer un personnel',
        message: 'Cette action est irréversible. Supprimer ce membre du personnel ?',
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.personnelService.deletePersonnel(id).subscribe(() => {
          this.loadPersonnels(); // recharge les données
          this.snackBar.open('Personnel supprimé avec succès !', 'Fermer', {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
          });
        });
      }
    });
  }

  loadPersonnels(): void {
    this.personnelService.getAllPersonnel().subscribe({
      next: (data) => {
        this.dataSource.data = data;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (err) => console.error('Erreur chargement personnels', err)
    });
  }




}
