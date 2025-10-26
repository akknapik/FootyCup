import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatchService } from '../../services/match.service';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { FormatService } from '../../services/format.service';
import { TournamentService } from '../../services/tournament.service';
import { UserRef } from '../../models/common/user-ref.model';
import { MatchResponse } from '../../models/match/match.response';
import { MatchItemResponse } from '../../models/match/match-item.response';

@Component({
  selector: 'app-match',
  standalone: false,
  templateUrl: './match.component.html',
  styleUrl: './match.component.css'
})
export class MatchComponent {
  tournamentId!: number;
  matches: MatchItemResponse[] = [];
  showGenerateButton = false;
  groups: any[] = [];
  pageSize = 10;
  currentPage = 1;
  isLoading: boolean = false;
  referees: UserRef[] = [];
  selectedReferees: Record<number, number | null> = {};
  assigning: Record<number, boolean> = {};


  constructor(private route: ActivatedRoute, public router: Router, private matchService: MatchService, public auth: AuthService, private notification: NotificationService, private formatService: FormatService, private tournamentService: TournamentService) {}

  ngOnInit(): void {
    this.tournamentId = +this.route.snapshot.paramMap.get('tournamentId')!;
    this.loadGroups();
    this.loadMatches();
    this.loadReferees();
  }

  loadMatches() {
    this.isLoading = true;
    this.matchService.getMatches(this.tournamentId).subscribe({
      next: (data) => {
        this.matches = data;
        this.matches.forEach(match => {
          if (match) {
            this.selectedReferees[match.id] = match.referee ? match.referee.id : null;
          }
        });
        this.isLoading = false;
      },
      error: () => {
        this.notification.showError('Error loading matches!');
        this.isLoading = false;
      },
    });
  }

  loadGroups() {
    this.formatService.getGroups(this.tournamentId).subscribe({
      next: (data) => this.groups = data,
      error: () => this.notification.showError('Failed to load groups'),
    });
  }

  loadReferees() {
    this.tournamentService.getReferees(this.tournamentId).subscribe({
      next: (data) => this.referees = data,
      error: () => this.notification.showError('Failed to load referees'),
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

  canManageEvents(match: MatchItemResponse | null): boolean {
    const currentUser = this.auth.currentUser;
    if (!match || !currentUser) {
      return false;
    }
    if (currentUser.userRole === 'ADMIN') {
      return true;
    }
    return match.referee?.id === currentUser.id;
  }

  openMatchEvents(match: MatchItemResponse | null): void {
    if (!match || !match.id) {
      return;
    }
    (match as any).showMenu = false;
    this.router.navigate([`/tournament/${this.tournamentId}/matches/${match.id}/events`]);
  }

  openTacticsBoard(match: MatchItemResponse | null): void {
    if (!match || !match.id) {
      return;
    }
    (match as any).showMenu = false;
    this.router.navigate([`/tournament/${this.tournamentId}/matches/${match.id}/tactics`]);
  }

  onRefereeSelected(match: MatchResponse, refereeId: number | null) {
    if (!match) {
      return;
    }

    if (refereeId === null) {
      this.selectedReferees[match.id] = match.referee ? match.referee.id ?? null : null;
      return;
    }

    this.assigning[match.id] = true;
    this.matchService.assignReferee(this.tournamentId, match.id, refereeId).subscribe({
      next: (updatedMatch) => {
        const index = this.matches.findIndex(m => m.id === updatedMatch.id);
        if (index !== -1) {
          this.matches[index] = updatedMatch;
        }
        this.selectedReferees[match.id] = updatedMatch.referee ? updatedMatch.referee.id ?? null : null;
        this.notification.showSuccess('Referee assigned successfully');
        this.assigning[match.id] = false;
      },
      error: () => {
        this.notification.showError('Failed to assign referee');
        this.selectedReferees[match.id] = match.referee ? match.referee.id ?? null : null;
        this.assigning[match.id] = false;
      }
    });
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

  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}
