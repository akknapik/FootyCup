import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TournamentItemResponse } from '../../models/tournament/tournament-item.response';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { TournamentService } from '../../services/tournament.service';
import { User } from '../../models/user.model';
import { Observable } from 'rxjs/internal/Observable';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  tournaments: TournamentItemResponse[] = [];
  isLoading = true;
  user$!: Observable<User | null>;

  constructor(
    private tournamentService: TournamentService,
    private router: Router,
    public auth: AuthService,
    private notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.user$ = this.auth.currentUser$;
    this.loadTournaments();
  }

  navigateHome(): void {
    this.router.navigate(['/dashboard']);
  }

  loadTournaments(): void {
    this.isLoading = true;
    this.tournamentService.getPublicTournaments().subscribe({
      next: (data) => {
        this.tournaments = data ?? [];
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.notification.showError('Unable to load public tournaments at the moment.');
      }
    });
  }

  viewDetails(id: number): void {
    this.router.navigate(['/tournaments', id]);
  }

  statusLabel(status: string): string {
    switch (status) {
      case 'ONGOING':
        return 'Ongoing';
      case 'FINISHED':
        return 'Finished';
      default:
        return 'Upcoming';
    }
  }

  statusClass(status: string): string {
    switch (status) {
      case 'ONGOING':
        return 'badge badge-live';
      case 'FINISHED':
        return 'badge badge-finished';
      default:
        return 'badge badge-upcoming';
    }
  }

  onCreateTournamentClick(): void {
  if (this.auth.isLoggedIn()) {
    this.router.navigate(['/tournaments/new']);
  } else {
    this.router.navigate(['/login']);
  }
}
}