import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TournamentService } from '../../../services/tournament.service';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { User } from '../../../models/user.model';
import { TournamentResponse } from '../../../models/tournament/tournament.response';
import { UserRef } from '../../../models/common/user-ref.model';

@Component({
  selector: 'app-tournament-details',
  standalone: false,
  templateUrl: './tournament-details.component.html',
  styleUrls: ['./tournament-details.component.css']
})
export class TournamentDetailsComponent implements OnInit {
  tournamentId!: number;
  tournament!: TournamentResponse;
  referees: UserRef[] = [];
  newRefereeEmail = '';
  currentUser: User | null = null;

  constructor(
    private route: ActivatedRoute,
    private tournamentService: TournamentService,
    private router: Router,
    public auth: AuthService,
    private notification: NotificationService
  ) {
    this.tournamentService.tournamentId$.subscribe((id) => {
      if (id) {
        this.tournamentId = +id;
        this.loadTournamentDetails(this.tournamentId);
      }
    }
    );
    this.auth.currentUser$.subscribe(user => this.currentUser = user);
  }

  ngOnInit(): void {
    this.tournamentId = +this.route.snapshot.paramMap.get('tournamentId')!;
    this.loadTournamentDetails(this.tournamentId);
  }

  goToTeams(): void {
    this.router.navigate(['/tournament', this.tournamentId, 'teams']);
  }

  goBack(): void {
    if (this.auth.isLoggedIn()) {
      this.router.navigate(['/tournaments/my']);
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  updateTournament() {
    if (!this.canManageTournament) {
      return;
    }
    const payload = {
      name: this.tournament.name,
      location: this.tournament.location,
      publicVisible: this.tournament.publicVisible
      };

    this.tournamentService.updateTournament(this.tournamentId, payload).subscribe({
      next: () => {
        this.notification.showSuccess('Tournament updated successfully!');
      },
      error: () => this.notification.showError('Error updating tournament')
    });
  }

  onAddReferee(email: string): void {
    if (!this.canManageTournament) {
      return;
    }
    const trimmedEmail = email.trim();

    if (!trimmedEmail) {
      this.notification.showError('Please provide referee email');
      return;
    }

    this.tournamentService.addReferee(this.tournamentId, trimmedEmail).subscribe({
      next: (referees) => {
        this.referees = referees;
        this.notification.showSuccess('Referee added successfully');
        this.newRefereeEmail = '';
      },
      error: () => this.notification.showError('Error adding referee')
    });
  }

  private loadTournamentDetails(id: number): void {
    const request$ = this.auth.isLoggedIn()
      ? this.tournamentService.getTournamentById(id)
      : this.tournamentService.getPublicTournamentById(id);

    request$.subscribe({
      next: (data) => {
        this.tournament = data;
        this.referees = data.referees ?? [];
        if (this.canManageTournament) {
          this.loadReferees(id);
        }
      },
      error: () => this.notification.showError('Error loading tournament details')
    });
  }

  private loadReferees(id: number): void {
    this.tournamentService.getReferees(id).subscribe({
      next: (data) => this.referees = data,
      error: () => this.notification.showError('Error loading referees')
    });
  }

  removeReferee(userId: number) {
    if (!this.canManageTournament) {
      return;
    }
    this.tournamentService.removeReferee(this.tournamentId, userId).subscribe({
      next: () => {
        this.notification.showSuccess('Referee removed');
        this.loadReferees(this.tournamentId);
        this.loadTournamentDetails(this.tournamentId);
      },
      error: () => this.notification.showError('Error removing referee')
    });
  }

  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }

  get canManageTournament(): boolean {
    return !!this.currentUser && !!this.tournament &&
      this.tournament.organizer?.id === this.currentUser.id;
  }
}
