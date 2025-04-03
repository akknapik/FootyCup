import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  form = {
    email: '',
    firstname: '',
    lastname: '',
    password: ''
  };

  constructor(private auth: AuthService, private router: Router) { }

  register() {
    this.auth.register(this.form).subscribe({
      next: (res) => {
        this.router.navigate(['/login']);
      },
      error: err => {
        alert(err?.error || 'Błąd rejestracji');
      }
    });
  }
}
