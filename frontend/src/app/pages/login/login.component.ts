import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  form = { email: '', password: '' };

  constructor(
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: NotificationService
  ) {}

  login() {
    this.auth.login(this.form).subscribe({
      next: () => {
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') || '/tournaments/my';
        this.router.navigateByUrl(returnUrl);
      },
      error: (err) => this.notification.showError(err?.error?.message ?? 'Login failed')
    });
  }
}
