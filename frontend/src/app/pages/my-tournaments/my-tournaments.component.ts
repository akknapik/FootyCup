import { Component } from '@angular/core';
import { TournamentService } from '../../services/tournament.service';
import { Route, Router } from '@angular/router';

@Component({
  selector: 'app-my-tournaments',
  standalone: false,
  templateUrl: './my-tournaments.component.html',
  styleUrl: './my-tournaments.component.css'
})
export class MyTournamentsComponent {
  tournaments: any[] = [];

  constructor(private tournamentService: TournamentService, private router: Router) {}

  ngOnInit(): void {
    this.loadTournaments();
  }

  loadTournaments() {
    this.tournamentService.getMyTournaments().subscribe({
      next: (data) => this.tournaments = data,
      error: () => alert('Błąd ładowania turniejów')
    });
  }

  goToCreate() {
    this.router.navigate(['/tournaments/new']);
  }

  deleteTournament(tournamentId: number) {
    if (confirm('Czy na pewno chcesz usunąć ten turniej?')) {
      this.tournamentService.deleteTournament(tournamentId).subscribe({
        next: () => {
          alert('Turniej usunięty!');
          this.loadTournaments();
        },
        error: () => alert('Błąd podczas usuwania turnieju')
      });
    }
  }
}
