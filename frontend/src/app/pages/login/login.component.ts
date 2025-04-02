import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';

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

  constructor(private auth: AuthService) { }

  login() {
    this.auth.login(this.form).subscribe({
      next: () => alert('Login successful!'),
      error: err => alert('Login failed') 
    });
  }


}
