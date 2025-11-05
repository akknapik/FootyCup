import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { User } from '../../models/user.model';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-settings',
  standalone: false,
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  private subscription?: Subscription;

  showNameModal = false;
  showPasswordModal = false;
  showDeleteModal = false;

  firstname = '';
  lastname = '';

  currentPassword = '';
  newPassword = '';
  confirmPassword = '';

  deletePassword = '';

  isSavingNames = false;
  isSavingPassword = false;
  isDeleting = false;

  constructor(
    public auth: AuthService,
    private userService: UserService,
    private notification: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.subscription = this.auth.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user && this.showNameModal) {
        this.firstname = user.firstname;
        this.lastname = user.lastname;
      }
    });
    if (!this.currentUser) {
      this.auth.loadCurrentUser().subscribe();
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  openNameModal(): void {
    if (!this.currentUser) { return; }
    this.firstname = this.currentUser.firstname;
    this.lastname = this.currentUser.lastname;
    this.showNameModal = true;
  }

  saveNames(): void {
    if (!this.firstname.trim() || !this.lastname.trim()) {
      this.notification.showError('Both firstname and lastname are required.');
      return;
    }
    this.isSavingNames = true;
    this.userService.updateProfile({
      firstname: this.firstname.trim(),
      lastname: this.lastname.trim()
    }).subscribe({
      next: (user) => {
        this.notification.showSuccess('Profile updated successfully.');
        this.showNameModal = false;
        this.isSavingNames = false;
        this.auth.loadCurrentUser().subscribe();
        this.currentUser = user;
      },
      error: (err) => {
        const message = err.error?.error || 'Failed to update profile.';
        this.notification.showError(message);
        this.isSavingNames = false;
      }
    });
  }

  openPasswordModal(): void {
    this.currentPassword = '';
    this.newPassword = '';
    this.confirmPassword = '';
    this.showPasswordModal = true;
  }

  changePassword(): void {
    if (!this.currentPassword || !this.newPassword || !this.confirmPassword) {
      this.notification.showError('All password fields are required.');
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.notification.showError('New password and confirmation must match.');
      return;
    }

    this.isSavingPassword = true;
    this.userService.changePassword({
      currentPassword: this.currentPassword,
      newPassword: this.newPassword,
      confirmPassword: this.confirmPassword
    }).subscribe({
      next: () => {
        this.notification.showSuccess('Password updated successfully.');
        this.showPasswordModal = false;
        this.isSavingPassword = false;
        this.currentPassword = '';
        this.newPassword = '';
        this.confirmPassword = '';
      },
      error: (err) => {
        const message = err.error?.error || 'Failed to update password.';
        this.notification.showError(message);
        this.isSavingPassword = false;
      }
    });
  }

  openDeleteModal(): void {
    this.deletePassword = '';
    this.showDeleteModal = true;
  }

  deleteAccount(): void {
    if (!this.deletePassword) {
      this.notification.showError('Please provide your password to confirm.');
      return;
    }

    this.isDeleting = true;
    this.userService.deleteAccount({ password: this.deletePassword }).subscribe({
      next: () => {
        this.notification.showSuccess('Your account has been deleted.');
        this.showDeleteModal = false;
        this.auth.logout().subscribe({
          next: () => this.router.navigate(['/dashboard']),
          error: () => this.router.navigate(['/login'])
        });
      },
      error: (err) => {
        const message = err.error?.error || 'Failed to delete account.';
        this.notification.showError(message);
        this.isDeleting = false;
      },
      complete: () => {
        this.showDeleteModal = false;
        this.isDeleting = false;
        this.deletePassword = '';
      }
    });
  }

  closeModals(): void {
    this.showNameModal = false;
    this.showPasswordModal = false;
    this.showDeleteModal = false;
  }

  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}