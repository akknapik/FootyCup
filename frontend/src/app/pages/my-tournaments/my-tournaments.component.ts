import { Component } from '@angular/core';
import { TournamentService } from '../../services/tournament.service';

@Component({
  selector: 'app-my-tournaments',
  standalone: false,
  templateUrl: './my-tournaments.component.html',
  styleUrl: './my-tournaments.component.css'
})
export class MyTournamentsComponent {
  tournaments: any[] = [];

  constructor(private tournamentService: TournamentService) {}

  ngOnInit(): void {
    console.log('Token w localStorage:', localStorage.getItem('token'));
    this.tournamentService.getMyTournaments().subscribe({
      next: (data) => this.tournaments = data,
      error: () => alert('Błąd ładowania turniejów')
    });
  }
}
