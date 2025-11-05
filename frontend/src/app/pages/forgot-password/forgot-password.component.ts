import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-forgot-password',
  standalone: false,
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  email = '';
  loading = false;
  success = false;

  constructor(
    private authService: AuthService,
    private notification: NotificationService
  ) {}

  submit(): void {
    if (!this.email || this.loading) {
      return;
    }

    this.loading = true;
    this.authService.requestPasswordReset(this.email).subscribe({
      next: () => {
        this.loading = false;
        this.success = true;
        this.notification.showSuccess('If an account exists for that email, a reset link has been sent.');
      },
      error: () => {
        this.loading = false;
        this.notification.showError('Could not send reset instructions. Please try again later.');
      }
    });
  }
}