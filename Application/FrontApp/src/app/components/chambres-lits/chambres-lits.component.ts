import { Component, OnInit } from '@angular/core';
import { Chambre, Lit } from '../../models';
import { EquipementsService } from '../../services/equipements.service';
import {MatAccordion, MatExpansionPanel, MatExpansionPanelTitle} from '@angular/material/expansion';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {ChambreFormComponent} from '../chambre-form/chambre-form.component';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '../confirm-dialog/confirm-dialog.component';
import {LitFormComponent} from '../lit-form/lit-form.component';

@Component({
  selector: 'app-chambres-lits',
  templateUrl: './chambres-lits.component.html',
  standalone:false,
  styleUrls: ['./chambres-lits.component.css']
})
export class ChambresLitsComponent implements OnInit {
  chambresParBloc: { [bloc: string]: Chambre[] } = {};
  litsParChambre: { [chambreId: number]: Lit[] } = {};
  loading = true;
  selectedStatut: string = '';
  searchNumero: string = '';


  constructor(private equipementsService: EquipementsService,private dialog: MatDialog) {}

  ngOnInit(): void {
    this.loadChambres();
  }

  chambres: Chambre[] = [];

  chargerChambres(): void {
    this.equipementsService.getChambres().subscribe((data) => {
      this.chambres = data;
    });
  }


  ouvrirFormChambre(chambre?: Chambre): void {
    const dialogRef = this.dialog.open(ChambreFormComponent, {
      width: '500px',
      data: { chambre }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) this.chargerChambres(); // ✅ Ici ça fonctionne car chargerChambres est dans le bon composant
    });
  }

  loadChambres(): void {
    this.equipementsService.getChambres().subscribe({
      next: (chambres) => {
        chambres.forEach((chambre) => {
          if (!this.chambresParBloc[chambre.bloc]) {
            this.chambresParBloc[chambre.bloc] = [];
          }
          this.chambresParBloc[chambre.bloc].push(chambre);
          this.loadLitsParChambre(chambre.id!);
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des chambres', err);
        this.loading = false;
      }
    });
  }
  getChambresFiltres(bloc: string): Chambre[] {
    let chambres = this.chambresParBloc[bloc];

    if (this.selectedStatut) {
      chambres = chambres.filter(ch => ch.statut === this.selectedStatut);
    }

    if (this.searchNumero.trim()) {
      chambres = chambres.filter(ch =>
        ch.numero.toLowerCase().includes(this.searchNumero.toLowerCase())
      );
    }

    return chambres;
  }

  supprimerChambre(chambre: Chambre): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Suppression de chambre',
        message: `Voulez-vous vraiment supprimer la chambre ${chambre.numero} ?`,
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.equipementsService.deleteChambre(chambre.id!).subscribe(() => {
          this.resetChambres(); // recharge les données après suppression
        });
      }
    });
  }

  resetChambres(): void {
    this.chambresParBloc = {};
    this.litsParChambre = {};
    this.loadChambres();
  }

  loadLitsParChambre(chambreId: number): void {
    this.equipementsService.getLitsByChambreId(chambreId).subscribe({
      next: (lits) => {
        this.litsParChambre[chambreId] = lits;
      },
      error: (err) => {
        console.error(`Erreur lors du chargement des lits pour la chambre ${chambreId}`, err);
      }
    });
  }



  modifierLit(lit: Lit): void {
    const dialogRef = this.dialog.open(LitFormComponent, {
      width: '400px',
      data: { lit }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.equipementsService.updateLit(lit.id!, result).subscribe(() => {
          this.loadLitsParChambre(lit.chambreId);
        });
      }
    });
  }
  ouvrirFormNouveauLit(chambre: Chambre): void {
    const dialogRef = this.dialog.open(LitFormComponent, {
      width: '400px',
      data: { lit: { numero: '', statut: 'Libre', chambreId: chambre.id! } }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.equipementsService.createLit(result).subscribe(() => {
          this.loadLitsParChambre(chambre.id!);
        });
      }
    });
  }


  supprimerLit(lit: Lit): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Suppression de lit',
        message: `Voulez-vous vraiment supprimer le lit ${lit.numero} ?`,
        confirmText: 'Supprimer',
        cancelText: 'Annuler'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.equipementsService.deleteLit(lit.id!).subscribe(() => {
          this.loadLitsParChambre(lit.chambreId);
        });
      }
    });
  }
  protected readonly Object = Object;


}
