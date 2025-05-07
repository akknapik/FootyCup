import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { NotificationService } from '../../services/notification.service';

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

  constructor(private auth: AuthService, private router: Router, private notification: NotificationService) { }

  login() {
    this.auth.login(this.form).subscribe({
      next: () =>  {
        this.router.navigate(['/tournaments/my']);
      },
      error: () => this.notification.showError('Login failed')
    });
  }


}
