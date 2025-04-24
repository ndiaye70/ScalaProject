import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';


import { DashboardComponent } from './components/dashboard/dashboard.component';
import {LoginComponent} from './login/login.component';
import {AuthInterceptor} from './services/jwt.interceptor';
import { RouterModule } from '@angular/router';
import {AppRoutingModule} from './app-routing.module';
import {MatToolbar, MatToolbarModule} from '@angular/material/toolbar';
import {MatButton, MatButtonModule} from '@angular/material/button';
import {MatDrawerContainer, MatSidenavModule} from '@angular/material/sidenav';
import {MatListItem, MatListModule, MatNavList} from '@angular/material/list';
import {MatTableModule} from '@angular/material/table';

import {MatSortModule} from '@angular/material/sort';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatCardModule} from '@angular/material/card';
import {MatMenuModule} from '@angular/material/menu';
import {MatIconModule} from '@angular/material/icon';
import { PatientsComponent } from './components/patients/patients.component';
import { PersonnelsComponent } from './components/personnels/personnels.component';

import { PaiementComponent } from './components/paiement/paiement.component';

import { DossierComponent } from './components/dossier/dossier.component';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import {MatDialogActions, MatDialogContent, MatDialogModule, MatDialogTitle} from '@angular/material/dialog';
import { AddPatientComponent } from './components/add-patient/add-patient.component';
import { MatSelectModule } from '@angular/material/select';
import {MAT_DATE_LOCALE, MatOptionModule, provideNativeDateAdapter} from '@angular/material/core';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { EditDossierComponent } from './components/edit-dossier/edit-dossier.component';
import {MatProgressBar, MatProgressBarModule} from '@angular/material/progress-bar';
import { ConsultationComponent } from './components/consultation/consultation.component';
import { HospitalisationComponent } from './components/hospitalisation/hospitalisation.component';
import {MatAccordion, MatExpansionModule, MatExpansionPanel, MatExpansionPanelTitle} from '@angular/material/expansion';
import {EditConsultationComponent} from './components/edit-consultation/edit-consultation.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { EditPersonnelComponent } from './components/edit-personnel/edit-personnel.component';
import { MatAutocompleteModule, MatAutocompleteTrigger} from '@angular/material/autocomplete';
import { EditHospitalisationComponent } from './components/edit-hospitalisation/edit-hospitalisation.component';
import { GardeComponent } from './components/garde/garde.component';
import {EditGardesComponent} from './components/edit-gardes/edit-gardes.component';
import { ChambresLitsComponent } from './components/chambres-lits/chambres-lits.component';
import { ChambreFormComponent } from './components/chambre-form/chambre-form.component';
import { EditPaiementComponent } from './components/edit-paiement/edit-paiement.component';
import {LitFormComponent} from './components/lit-form/lit-form.component';
import {RendezVousComponent} from './components/rendez-vous/rendez-vous.component';
import { EditRendezVousComponent } from './components/edit-rendez-vous/edit-rendez-vous.component';
import {EquipementsService} from './services/equipements.service';
import {MaterielsComponent} from './components/materiels/materiels.component';
import { EditMaterielsComponent } from './components/edit-materiels/edit-materiels.component';







@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    LoginComponent,
    PatientsComponent,
    PersonnelsComponent,
    PaiementComponent,
    RendezVousComponent,
    DossierComponent,
    ConfirmDialogComponent,
    AddPatientComponent,
    EditDossierComponent,
    ConsultationComponent,
    HospitalisationComponent,
    EditConsultationComponent,
    EditPersonnelComponent,
    EditHospitalisationComponent,
    GardeComponent,
    EditGardesComponent,
    ChambresLitsComponent,
    ChambreFormComponent,
    EditPaiementComponent,
    LitFormComponent,
    RendezVousComponent,
    EditRendezVousComponent,
    MaterielsComponent,
    EditMaterielsComponent

  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule,
    AppRoutingModule,
    MatToolbarModule,
    MatButtonModule,
    MatMenuModule,
    MatDrawerContainer,
    MatListModule,
    MatSidenavModule,
    MatCardModule,
    MatInputModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
    MatDialogContent,
    MatDialogTitle,
    MatDialogActions,
    MatDialogModule,
    MatSelectModule,
    MatOptionModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    MatExpansionModule,
    MatDatepickerModule,
    MatInputModule,
    MatAutocompleteTrigger,
    MatAutocompleteModule,
    MatExpansionPanel,
    MatAccordion


  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    provideNativeDateAdapter(),
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' }
  ],

  bootstrap: [AppComponent]
})
export class AppModule { }
