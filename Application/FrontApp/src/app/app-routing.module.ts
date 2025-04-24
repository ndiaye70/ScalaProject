import { DashboardComponent } from './components/dashboard/dashboard.component';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {NgModule} from '@angular/core';
import {PatientsComponent} from './components/patients/patients.component';
import {PersonnelsComponent} from './components/personnels/personnels.component';

import {AddPatientComponent} from './components/add-patient/add-patient.component';
import {DossierComponent} from './components/dossier/dossier.component';
import {EditDossierComponent} from './components/edit-dossier/edit-dossier.component';
import {ConsultationComponent} from './components/consultation/consultation.component';
import {HospitalisationComponent} from './components/hospitalisation/hospitalisation.component';
import {EditConsultationComponent} from './components/edit-consultation/edit-consultation.component';
import {EditPersonnelComponent} from './components/edit-personnel/edit-personnel.component';
import {EditHospitalisationComponent} from './components/edit-hospitalisation/edit-hospitalisation.component';
import {GardeComponent} from './components/garde/garde.component';
import {EditGardesComponent} from './components/edit-gardes/edit-gardes.component';
import {ChambresLitsComponent} from './components/chambres-lits/chambres-lits.component';
import {PaiementComponent} from './components/paiement/paiement.component';
import {EditPaiementComponent} from './components/edit-paiement/edit-paiement.component';
import {RendezVousComponent} from './components/rendez-vous/rendez-vous.component';
import {EditRendezVousComponent} from './components/edit-rendez-vous/edit-rendez-vous.component';
import {MaterielsComponent} from './components/materiels/materiels.component';
import {EditMaterielsComponent} from './components/edit-materiels/edit-materiels.component';

const routes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children:[
      { path: 'patients', component: PatientsComponent },
      { path: 'paiements', component: PaiementComponent },
      { path: 'personnels', component: PersonnelsComponent },
      { path: 'materiels', component:MaterielsComponent },
      { path: 'editpersonnel/:id', component: EditPersonnelComponent },
      { path: 'gardes', component: GardeComponent },
      { path: 'rendez-vous', component:RendezVousComponent },
      {path: 'add-rendez-vous',
        component: EditRendezVousComponent},
      {path: 'edit-rendez-vous/:id',
        component: EditRendezVousComponent},

      {path: 'add-materiel',
        component: EditMaterielsComponent},
      {path: 'edit-materiel/:id',
        component: EditMaterielsComponent},

      { path: 'chambres-lits', component: ChambresLitsComponent },
      {path: 'addGarde',
        component: EditGardesComponent},
      {path: 'editGarde/:id',
        component: EditGardesComponent},
      { path: 'dossier/:id', component: DossierComponent },
      {path: 'edit-dossier/:id',
        component: EditDossierComponent},
      {path: 'consulter/:id',
        component: ConsultationComponent},
      {path: 'editConsultation/:idConsultation',
        component: EditConsultationComponent},
      {path: 'addConsultation/:idPatient',
        component: EditConsultationComponent},
      {path: 'hospitaliser/:id',
        component: HospitalisationComponent},
      {path: 'editHospitalisation/:id',
        component: EditHospitalisationComponent},
      {path: 'addHospitalisation/:id',
        component: EditHospitalisationComponent},
      { path: 'paiements/add', component: EditPaiementComponent },
      { path: 'paiements/edit/:id', component: EditPaiementComponent },

      {
        path: 'patients/add',
        component: AddPatientComponent
      },
      {
        path: 'patients/edit/:id',
        component: AddPatientComponent
      }
    ]
  },
  { path: 'login', component: LoginComponent }, // page sans dashboard
  { path: '**', redirectTo: 'patients' }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
