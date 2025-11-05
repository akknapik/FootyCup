import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-reset-password',
  standalone: false,
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent {
  token: string | null;
  password = '';
  confirmPassword = '';
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private notification: NotificationService
  ) {
    this.token = this.route.snapshot.queryParamMap.get('token');
  }

  get passwordMismatch(): boolean {
    return !!this.password && !!this.confirmPassword && this.password !== this.confirmPassword;
  }

  submit(): void {
    if (!this.token || this.loading || this.passwordMismatch) {
      return;
    }

    this.loading = true;
    this.authService.resetPassword(this.token, this.password).subscribe({
      next: () => {
        this.loading = false;
        this.notification.showSuccess('Your password has been updated. You can now log in.');
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.loading = false;
        const message = error?.error ?? 'Could not reset password. The link may have expired.';
        this.notification.showError(message);
      }
    });
  }
}