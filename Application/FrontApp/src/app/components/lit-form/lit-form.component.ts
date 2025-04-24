import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Lit } from '../../models';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-lit-form',
  templateUrl: './lit-form.component.html',
  styleUrls: ['./lit-form.component.css'],
  standalone:false
})
export class LitFormComponent {
  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<LitFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { lit: Lit }
  ) {
    this.form = this.fb.group({
      numero: [data.lit.numero, Validators.required],
      statut: [data.lit.statut, Validators.required],
    });
  }

  save(): void {
    const updated = { ...this.data.lit, ...this.form.value };
    this.dialogRef.close(updated);
  }

  close(): void {
    this.dialogRef.close();
  }
}
