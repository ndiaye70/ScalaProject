import { Component } from '@angular/core';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  standalone: false,
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {

  constructor(private authservice:AuthService ) {
  }
  logout() {


    this.authservice.logout();

  }
}
