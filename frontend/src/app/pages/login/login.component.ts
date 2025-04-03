import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  form = {
    email: '',
    password: ''
  };

  constructor(private auth: AuthService, private router: Router) { }

  login() {
    this.auth.login(this.form).subscribe({
      next: () =>  {
        this.router.navigate(['/tournaments/my']);
      },
      error: err => alert('Login failed') 
    });
  }


}
