import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';

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

  constructor(private auth: AuthService) { }

  register() {
    this.auth.register(this.form).subscribe({
      next: (res) => {
        console.log('Rejestracja OK:', res);
        alert('Zarejestrowano!');
      },
      error: err => {
        console.error('Błąd rejestracji:', err);
        alert(err?.error || 'Błąd rejestracji');
      }
    });
  }
}
