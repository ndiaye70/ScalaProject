import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import html2pdf from 'html2pdf.js';




import { PaiementService } from '../../services/paiement.service';
import { PatientService, Patient } from '../../services/patient-service.service';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import {Paiement} from "../../models";

@Component({
  selector: 'app-paiement',
  templateUrl: './paiement.component.html',
  styleUrls: ['./paiement.component.css'],
  standalone:false
})
export class PaiementComponent implements OnInit {
  displayedColumns: string[] = ['id', 'patient', 'montant', 'mode', 'datePaiement', 'reference', 'actions'];
  dataSource = new MatTableDataSource<Paiement>();
  patientsMap = new Map<number, Patient>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
      private paiementService: PaiementService,
      private patientService: PatientService,
      private dialog: MatDialog,
      private snackBar: MatSnackBar,
      private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.patientService.getAll().subscribe({
      next: patients => {
        patients.forEach(p => this.patientsMap.set(p.id!, p));
        this.loadPaiements();
      },
      error: err => console.error('Erreur chargement patients', err)
    });
  }

  loadPaiements(): void {
    this.paiementService.getAll().subscribe({
      next: paiements => {
        this.dataSource = new MatTableDataSource(paiements);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: err => console.error('Erreur chargement paiements', err)
    });
  }

  getPatientFullName(patientId: number): string {
    const patient = this.patientsMap.get(patientId);
    return patient ? `${patient.nom} ${patient.prenom}` : 'Patient inconnu';
  }

  deletePaiement(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Supprimer un paiement',
        message: 'Cette action est irréversible. Supprimer ce paiement ?',
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.paiementService.delete(id).subscribe(() => {
          this.loadPaiements();
          this.snackBar.open('Paiement supprimé avec succès !', 'Fermer', {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
          });
        });
      }
    });
  }

  ajouterPaiement(): void {
    this.router.navigate(['/paiements/add']);
  }

  modifierPaiement(id: number): void {
    this.router.navigate(['/paiements/edit', id]);
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  downloadFacture(paiement: Paiement): void {
    const patient = this.patientsMap.get(paiement.patientId);
    const factureElement = document.getElementById(`facture-${paiement.id}`);

    if (factureElement) {
      // 👀 Le rendre visible temporairement
      factureElement.style.display = 'block';

      const opt = {
        margin:       0.5,
        filename:     `facture_${patient?.nom}_${paiement.id}.pdf`,
        image:        { type: 'jpeg', quality: 0.98 },
        html2canvas:  { scale: 2 },
        jsPDF:        { unit: 'in', format: 'letter', orientation: 'portrait' }
      };

      html2pdf().set(opt).from(factureElement).save().then(() => {
        // 🔙 Le cacher après génération
        factureElement.style.display = 'none';
      });
    }
  }

}
