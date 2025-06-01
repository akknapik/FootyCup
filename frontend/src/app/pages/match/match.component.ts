import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatchService } from '../../services/match.service';
import { Match } from '../../models/match.model';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { FormatService } from '../../services/format.service';

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
  groups: any[] = [];
  pageSize = 10;
  currentPage = 1;

  constructor(private route: ActivatedRoute, private matchService: MatchService, public auth: AuthService, private notification: NotificationService, private formatService: FormatService) {}

  ngOnInit(): void {
    this.tournamentId = +this.route.snapshot.paramMap.get('tournamentId')!;
    this.loadGroups();
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

  loadGroups() {
  this.formatService.getGroups(this.tournamentId).subscribe({
    next: (data) => this.groups = data,
    error: () => this.notification.showError('Failed to load groups'),
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
 
  toggleMenu(match: any): void {
  this.paginatedMatches.forEach(m => {
    if (m && m.id !== match.id) m.showMenu = false;
  });
  match.showMenu = !match.showMenu;
}

  get paginatedMatches(): (any | null)[] {
    const start = (this.currentPage - 1) * this.pageSize;
    const page = this.matches.slice(start, start + this.pageSize);
    const empty = this.pageSize - page.length;
    return [...page, ...Array(empty).fill(null)];
  }

  get totalPages(): number {
    return Math.ceil((this.matches.length || 1) / this.pageSize);
  }

  prevPage() {
    if (this.currentPage > 1) this.currentPage--;
  }

  nextPage() {
    if (this.currentPage < this.totalPages) this.currentPage++;
  }


}
