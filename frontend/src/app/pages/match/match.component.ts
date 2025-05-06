import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatchService } from '../../services/match.service';
import { Match } from '../../models/match.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-match',
  standalone: false,
  templateUrl: './match.component.html',
  styleUrl: './match.component.css'
})
export class MatchComponent {
  tournamentId!: number;
  matches: Match[] = [];
  showGenerateButton = false;
  rematch = false;
  constructor(private route: ActivatedRoute, private matchService: MatchService, public auth: AuthService) {}

  ngOnInit(): void {
    this.tournamentId = +this.route.snapshot.paramMap.get('tournamentId')!;
    this.loadMatches();
  }

  loadMatches() {
    this.matchService.getMatches(this.tournamentId).subscribe({
      next: (data) => {
        console.log(data);
        this.matches = data;
      },
      error: () => alert('Błąd ładowania meczów'),
    });
  }

  deleteMatch(matchId: number) {
    if (!confirm('Czy na pewno usunąć mecz?')) return;
    this.matchService.deleteMatch(this.tournamentId, matchId).subscribe({
      next: () => this.loadMatches(),
      error: () => alert('Błąd usuwania meczu'),
    });
  }

  generateGroupMatches() {
    if (!confirm('Czy na pewno wygenerować mecze?')) return;
    this.matchService.generateGroupMatches(this.tournamentId).subscribe({
      next: () => {
        this.loadMatches();
      },
      error: () => alert('Błąd generowania meczów'),
    });
  }
}
