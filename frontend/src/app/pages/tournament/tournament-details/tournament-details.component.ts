import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TournamentService } from '../../../services/tournament.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-tournament-details',
  standalone: false,
  templateUrl: './tournament-details.component.html',
  styleUrls: ['./tournament-details.component.css']
})
export class TournamentDetailsComponent implements OnInit {
  tournamentId!: number;
  tournament: any;

  constructor(
    private route: ActivatedRoute,
    private tournamentService: TournamentService,
    private router: Router,
    public auth: AuthService
  ) {
    this.tournamentService.tournamentId$.subscribe((id) => {
      if (id) {
        this.tournamentId = +id;
        this.tournamentService.getTournamentById(this.tournamentId).subscribe({
          next: (data) => this.tournament = data,
          error: () => alert('Błąd ładowania szczegółów turnieju')
        });
      }
    }
    );
  }

  ngOnInit(): void {
    this.tournamentId = +this.route.snapshot.paramMap.get('tournamentId')!;
    this.tournamentService.getTournamentById(this.tournamentId).subscribe({
      next: (data) => this.tournament = data,
      error: () => alert('Błąd ładowania szczegółów turnieju')
    });
  }

  goToTeams(): void {
    this.router.navigate(['/tournament', this.tournamentId, 'teams']);
  }

  goBack(): void {
    this.router.navigate(['/tournaments/my']);
  }
}
