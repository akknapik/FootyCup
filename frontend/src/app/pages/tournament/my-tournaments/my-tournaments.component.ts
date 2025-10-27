import { Component } from '@angular/core';
import { TournamentService } from '../../../services/tournament.service';
import { Route, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { TournamentItemResponse } from '../../../models/tournament/tournament-item.response';
import { MyTournamentsResponse } from '../../../models/tournament/my-tournaments.response';

@Component({
  selector: 'app-my-tournaments',
  standalone: false,
  templateUrl: './my-tournaments.component.html',
  styleUrl: './my-tournaments.component.css'
})
export class MyTournamentsComponent {
  organized: TournamentItemResponse[] = [];
  refereeing: TournamentItemResponse[] = [];
  coaching: TournamentItemResponse[] = [];
  observing: TournamentItemResponse[] = [];
  isLoading: boolean = false;

  constructor(private tournamentService: TournamentService, private router: Router, public auth: AuthService, private notification: NotificationService) {}

  ngOnInit(): void {
    this.loadTournaments();
  }

  loadTournaments() {
    this.isLoading = true;
    this.tournamentService.getMyTournaments().subscribe({
      next: (data: MyTournamentsResponse) => {
        this.organized = data.organized ?? [];
        this.refereeing = data.refereeing ?? [];
        this.coaching = data.coaching ?? [];
        this.observing = data.observing ?? [];
        this.openedMenu = null;
        this.isLoading = false;
      },
      error: () => {
        this.notification.showError('Error loading tournaments');
        this.isLoading = false;
      }
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

  get hasAnyTournaments(): boolean {
    return this.organized.length > 0 || this.refereeing.length > 0 || this.coaching.length > 0 || this.observing.length > 0;
  }

  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']); 
    });
  }
}
