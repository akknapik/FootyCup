import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { NotificationService } from '../../services/notification.service';

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
    password: '',
    confirmPassword: ''
  };

  constructor(private auth: AuthService, private router: Router, private notification: NotificationService) { }

  register() {
    if (this.form.password !== this.form.confirmPassword) {
      this.notification.showError('Passwords do not match!');
      return;
    }
    this.auth.register(this.form).subscribe({
      next: (res) => {
        this.router.navigate(['/login']);
      },
      error: err => {
        this.notification.showError(err?.error || 'Registration failed!');
      }
    });
  }
}
