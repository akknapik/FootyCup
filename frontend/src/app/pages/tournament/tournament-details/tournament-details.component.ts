import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TournamentService } from '../../../services/tournament.service';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-tournament-details',
  standalone: false,
  templateUrl: './tournament-details.component.html',
  styleUrls: ['./tournament-details.component.css']
})
export class TournamentDetailsComponent implements OnInit {
  tournamentId!: number;
  tournament: any;
  referees: User[] = [];
  newRefereeEmail = '';
  
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
  }

  ngOnInit(): void {
    this.tournamentId = +this.route.snapshot.paramMap.get('tournamentId')!;
    this.loadTournamentDetails(this.tournamentId);
  }

  goToTeams(): void {
    this.router.navigate(['/tournament', this.tournamentId, 'teams']);
  }

  goBack(): void {
    this.router.navigate(['/tournaments/my']);
  }

  updateTournament() {
    const payload = {
      name: this.tournament.name,
      location: this.tournament.location
    };

    this.tournamentService.updateTournament(this.tournamentId, payload).subscribe({
      next: () => {
        this.notification.showSuccess('Tournament updated successfully!');
      },
      error: () => this.notification.showError('Error updating tournament')
    });
  }

 onAddReferee(email: string): void {
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
    this.tournamentService.getTournamentById(id).subscribe({
      next: (data) => this.tournament = data,
      error: () => this.notification.showError('Error loading tournament details')
    });

    this.loadReferees(id);
  }

  private loadReferees(id: number): void {
    this.tournamentService.getReferees(id).subscribe({
      next: (data) => this.referees = data,
      error: () => this.notification.showError('Error loading referees')
    });
  }

  removeReferee(userId: number) {
    this.tournamentService.removeReferee(this.tournamentId, userId).subscribe({
      next: (t) => {
        this.tournament = t;
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
}
