import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../../models/user.model';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-admin-users',
  standalone: false,
  templateUrl: './admin-users.component.html',
  styleUrl: './admin-users.component.css'
})
export class AdminUsersComponent implements OnInit {
  users: User[] = [];
  currentPage = 1;
  pageSize = 10;

  constructor(
    private userService: UserService,
    private notification: NotificationService,
    public auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.auth.currentUser;
    if (!user || user.userRole !== 'ADMIN') {
      this.router.navigate(['/tournaments/my']); 
      return;
    }

    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getUsers().subscribe({
      next: users => this.users = users,
      error: () => this.notification.showError('Failed to load users')
    });
  }

deleteUser(id: number): void {
  this.notification.confirm('Are you sure you want to delete this user?').subscribe(confirmed => {
    if (!confirmed) return;

    this.userService.deleteUser(id).subscribe({
      next: () => {
        if (this.auth.currentUser?.id === id) {
          this.notification.showSuccess('Your account has been deleted.');
          this.auth.logout().subscribe(() => this.router.navigate(['/login']));
        } else {
          this.notification.showSuccess('User deleted.');
          this.loadUsers();
        }
      },
      error: () => {
        this.notification.showError('Failed to delete user');
      }
    });
  });
}


get totalPages(): number {
  return Math.ceil((this.users?.length || 0) / this.pageSize) || 1;
}

get paginatedUsers(): (any | null)[] {
  const start = (this.currentPage - 1) * this.pageSize;
  const sliced = this.users.slice(start, start + this.pageSize);

  const fillCount = this.pageSize - sliced.length;
  return [...sliced, ...Array(fillCount).fill(null)];
}

prevPage() {
  if (this.currentPage > 1) {
    this.currentPage--;
  }
}

nextPage() {
  if (this.currentPage < this.totalPages) {
    this.currentPage++;
  }
}

toggleMenu(user: any): void {
  this.paginatedUsers.forEach(u => {
    if (u && u.id !== user.id) u.showMenu = false;
  });
  user.showMenu = !user.showMenu;
}


    logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']); 
    });
  }
}
