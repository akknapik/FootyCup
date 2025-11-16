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
  allTournaments: TournamentItemResponse[] = [];
  isLoading: boolean = false;
  searchCode = '';
  searchResult: TournamentItemResponse | null = null;
  isSearching = false;
  searchError: string | null = null;

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
        this.allTournaments = data.allTournaments ?? [];
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

  openDetails(id: number, code?: string): void {
    if (code) {
      this.router.navigate(['/tournaments', id], { queryParams: { code } });
      return;
    }
    this.router.navigate(['/tournaments', id]);
  }

  onSearchCodeChange(): void {
    this.searchError = null;
    this.searchResult = null;
  }

  searchByCode(): void {
    const trimmed = this.searchCode.trim();
    if (!trimmed) {
      this.searchError = 'Enter a tournament code to search.';
      this.searchResult = null;
      return;
    }

    this.isSearching = true;
    this.searchError = null;
    this.tournamentService.searchTournamentByCode(trimmed).subscribe({
      next: (result) => {
        this.searchResult = result;
        this.isSearching = false;
      },
      error: () => {
        this.searchResult = null;
        this.searchError = 'Tournament not found.';
        this.isSearching = false;
      }
    });
  }

  get hasAnyTournaments(): boolean {
    const baseLists = this.organized.length > 0 || this.refereeing.length > 0
      || this.coaching.length > 0 || this.observing.length > 0;
    if (this.isAdminUser()) {
      return baseLists || this.allTournaments.length > 0;
    }
    return baseLists;
  }

  isAdminUser(): boolean {
    return this.auth.currentUser?.userRole === 'ADMIN';
  }

  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']); 
    });
  }
}
