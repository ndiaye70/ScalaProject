import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';

import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import {Materiel} from '../../models';
import {EquipementsService} from '../../services/equipements.service';

@Component({
  selector: 'app-materiels',
  templateUrl: './materiels.component.html',
  styleUrls: ['./materiels.component.css'],
  standalone: false
})
export class MaterielsComponent implements OnInit {
  displayedColumns: string[] = ['id', 'nom', 'typeMateriel', 'statut', 'quantite', 'dateAchat', 'fournisseur', 'actions'];
  dataSource = new MatTableDataSource<Materiel>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private equipementService: EquipementsService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMateriels();
  }

  loadMateriels(): void {
    this.equipementService.getAll().subscribe({
      next: (materiels) => {
        this.dataSource = new MatTableDataSource<Materiel>(materiels);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (err) => {
        console.error('Erreur chargement matériels', err);
        this.snackBar.open('Erreur de chargement des matériels', 'Fermer', { duration: 3000 });
      }
    });
  }

  deleteMateriel(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Supprimer un matériel',
        message: 'Êtes-vous sûr de vouloir supprimer ce matériel ?',
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.equipementService.delete(id).subscribe(() => {
          this.loadMateriels();
          this.snackBar.open('Matériel supprimé', 'Fermer', {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
          });
        });
      }
    });
  }

  ajouterMateriel(): void {
    this.router.navigate(['/add-materiel']);
  }

  editMateriel(id: number): void {
    this.router.navigate(['/edit-materiel', id]);
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
}
