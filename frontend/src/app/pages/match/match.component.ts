import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatchService } from '../../services/match.service';
import { Match } from '../../models/match.model';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';

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
  constructor(private route: ActivatedRoute, private matchService: MatchService, public auth: AuthService, private notification: NotificationService) {}

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
      error: () => this.notification.showError('Error loading matches!'),
    });
  }

  deleteMatch(matchId: number) {
    this.notification.confirm('Are you sure you want to delete this match?').subscribe((confirmed) => {
      if (confirmed) {
        this.matchService.deleteMatch(this.tournamentId, matchId).subscribe({
          next: () => this.loadMatches(),
          error: () => this.notification.showError('Error deleting match!'),
        });
      }
    });
  }

  generateGroupMatches() {
    this.notification.confirm('Are you sure you want to generate group matches?').subscribe((confirmed) => {
      if (confirmed) {
        this.matchService.generateGroupMatches(this.tournamentId).subscribe({
          next: () => {
            this.loadMatches();
          },
          error: () => this.notification.showError('Error generating matches!'),
        });
      }
    });
  }
}
