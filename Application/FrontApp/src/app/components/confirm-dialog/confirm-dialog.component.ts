import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-dialog',
  standalone: false,
  template: `
    <h2 mat-dialog-title>{{ data.title || 'Confirmation' }}</h2>

    <mat-dialog-content>
      {{ data.message || 'Êtes-vous sûr de vouloir effectuer cette action ?' }}
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">
        {{ data.cancelText || 'Annuler' }}
      </button>
      <button mat-button color="warn" (click)="onConfirm()">
        {{ data.confirmText || 'Confirmer' }}
      </button>
    </mat-dialog-actions>
  `
})
export class ConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
