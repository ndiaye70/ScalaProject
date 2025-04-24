import { Component, OnInit, ViewChild } from '@angular/core';
import { Garde, Personnel } from '../../models';
import { PersonnelServiceService } from '../../services/personnel-service.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-garde',
  templateUrl: './garde.component.html',
  styleUrls: ['./garde.component.css'],
  standalone: false
})
export class GardeComponent implements OnInit {

  gardes: Garde[] = [];
  personnels: { [id: number]: Personnel } = {};
  dataSource = new MatTableDataSource<Garde>();
  displayedColumns: string[] = ['id', 'nom', 'prenom', 'type', 'specialite', 'telephone', 'heureDebut', 'heureFin', 'actions'];
  loading: boolean = true;
  error: string | null = null;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private personnelService: PersonnelServiceService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.chargerGardes();
  }

  chargerGardes(): void {
    this.loading = true;
    this.error = null;

    this.personnelService.getAllGardes().subscribe({
      next: (data) => {
        this.gardes = data;
        this.dataSource.data = data;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;

        const uniquePersonnelIds = Array.from(new Set(data.map(g => g.personnelId)));
        uniquePersonnelIds.forEach(id => this.chargerPersonnel(id));
        this.loading = false;
      },
      error: () => {
        this.error = 'Erreur lors du chargement des gardes.';
        this.loading = false;
      }
    });
  }

  chargerPersonnel(id: number): void {
    this.personnelService.getPersonnelById(id).subscribe({
      next: (personnel) => {
        this.personnels[id] = personnel;
      },
      error: () => {
        console.warn(`Impossible de charger le personnel avec l'ID ${id}`);
      }
    });
  }

  supprimerGarde(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Supprimer une garde',
        message: 'Cette action est irréversible. Supprimer cette garde ?',
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.personnelService.supprimerGarde(id).subscribe(() => {
          this.gardes = this.gardes.filter(g => g.id !== id);
          this.dataSource.data = this.gardes;
          this.snackBar.open('Garde supprimée avec succès !', 'Fermer', {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
          });
        });
      }
    });
  }

  modifierGarde(id: number | undefined): void {
    if (id !== undefined) {
      this.router.navigate(['/editGarde', id]);
    }
  }

  ajouterGarde(): void {
    this.router.navigate(['/addGarde']);
  }

  applyFilter(event: KeyboardEvent): void {
    const filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.dataSource.filter = filterValue;
  }
}
