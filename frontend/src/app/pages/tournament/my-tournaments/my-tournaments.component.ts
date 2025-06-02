import { Component } from '@angular/core';
import { TournamentService } from '../../../services/tournament.service';
import { Route, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-my-tournaments',
  standalone: false,
  templateUrl: './my-tournaments.component.html',
  styleUrl: './my-tournaments.component.css'
})
export class MyTournamentsComponent {
  tournaments: any[] = [];

  constructor(private tournamentService: TournamentService, private router: Router, public auth: AuthService, private notification: NotificationService) {}

  ngOnInit(): void {
    this.loadTournaments();
  }

  loadTournaments() {
    this.tournamentService.getMyTournaments().subscribe({
      next: (data) => this.tournaments = data,
      error: () => this.notification.showError('Error loading tournaments')
    });
  }

  goToCreate() {
    this.router.navigate(['/tournaments/new']);
  }

  deleteTournament(tournamentId: number) {
    this.notification.confirm('Are you sure you want to delete this tournament?')
      .subscribe(confirmed => {
        if (confirmed) {
          this.tournamentService.deleteTournament(tournamentId).subscribe({
            next: () => {
              this.notification.showSuccess('Tournament deleted!');
              this.loadTournaments();
            },
            error: () => this.notification.showError('Error deleting tournament')
          });
        }
      });
  }
  
  openedMenu: number | null = null;

  toggleMenu(id: number): void {
    this.openedMenu = this.openedMenu === id ? null : id;
  }

  openDetails(id: number): void {
    this.router.navigate(['/tournaments', id]);
  }

  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']); 
    });
  }
}
