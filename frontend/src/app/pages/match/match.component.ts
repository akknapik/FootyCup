import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatchService } from '../../services/match.service';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { FormatService } from '../../services/format.service';
import { TournamentService } from '../../services/tournament.service';
import { UserRef } from '../../models/common/user-ref.model';
import { MatchResponse } from '../../models/match/match.response';
import { MatchItemResponse } from '../../models/match/match-item.response';
import { TournamentResponse } from '../../models/tournament/tournament.response';
import { User } from '../../models/user.model';
import { Subscription } from 'rxjs';
import { TeamService } from '../../services/team.service';

@Component({
  selector: 'app-match',
  standalone: false,
  templateUrl: './match.component.html',
  styleUrl: './match.component.css'
})
export class MatchComponent implements OnInit, OnDestroy {
  tournamentId!: number;
  matches: MatchItemResponse[] = [];
  groups: any[] = [];
  pageSize = 10;
  currentPage = 1;
  isLoading = false;
  referees: UserRef[] = [];
  selectedReferees: Record<number, number | null> = {};
  assigning: Record<number, boolean> = {};
  tournament: TournamentResponse | null = null;
  currentUser: User | null = null;
  canManageMatches = false;
  isForbidden = false;

  private userSubscription?: Subscription;

  teamCoachMap: Record<number, number | null> = {};
  private coachRequests = new Set<number>();

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private matchService: MatchService,
    public auth: AuthService,
    private notification: NotificationService,
    private formatService: FormatService,
    private tournamentService: TournamentService,
    private teamService: TeamService
  ) {}
  
  ngOnInit(): void {
    this.tournamentId = +this.route.snapshot.paramMap.get('tournamentId')!;
    this.userSubscription = this.auth.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.updateManagementState();
    });

    this.loadTournament();
    this.loadGroups();
    this.loadMatches();
  }

  ngOnDestroy(): void {
    this.userSubscription?.unsubscribe();
  }

  private loadTournament(): void {
    this.tournamentService.getTournamentById(this.tournamentId).subscribe({
      next: tournament => {
        this.tournament = tournament;
        this.updateManagementState();
      },
      error: err => {
        this.tournament = null;
        this.updateManagementState();
        if (err.status === 403) {
          this.notification.showInfo('You can view matches, but you do not have permissions to manage this tournament.');
        } else {
          this.notification.showError('Failed to load tournament details.');
        }
      }
    });
  }

  loadMatches(): void {
    this.isLoading = true;
    this.matchService.getMatches(this.tournamentId).subscribe({
      next: data => {
        this.matches = data;
        this.isForbidden = false;
        this.selectedReferees = {};
        this.matches.forEach(match => {
          if (match) {
            this.selectedReferees[match.id] = match.referee ? match.referee.id : null;
          }
        });
        this.refreshTeamCoachCache();
        this.isLoading = false;
      },
      error: err => {
        this.isLoading = false;
        if (err.status === 403) {
          this.matches = [];
          this.isForbidden = true;
          this.notification.showInfo('You do not have access to view matches in this tournament.');
        } else {
          this.notification.showError('Error loading matches!');
        }
      }
    });
  }

  loadGroups(): void {
    this.formatService.getGroups(this.tournamentId).subscribe({
      next: data => (this.groups = data),
      error: () => this.notification.showError('Failed to load groups')
    });
  }

  private loadReferees(): void {
    if (!this.canManageMatches) {
      return;
    }
    this.tournamentService.getReferees(this.tournamentId).subscribe({
      next: data => (this.referees = data),
      error: err => {
        if (err.status === 403) {
          this.notification.showInfo('Only the organizer can manage referees for this tournament.');
        } else {
          this.notification.showError('Failed to load referees');
        }
      }
    });
  }

  deleteMatch(matchId: number): void {
    if (!this.canManageMatches) {
      this.notification.showInfo('Only the organizer can delete matches.');
      return;
    }
    this.notification.confirm('Are you sure you want to delete this match?').subscribe(confirmed => {
      if (confirmed) {
        this.matchService.deleteMatch(this.tournamentId, matchId).subscribe({
          next: () => this.loadMatches(),
          error: () => this.notification.showError('Error deleting match!')
        });
      }
    });
  }

  generateGroupMatches(): void {
    if (!this.canManageMatches) {
      this.notification.showInfo('Only the organizer can generate group matches.');
      return;
    }
    this.notification.confirm('Are you sure you want to generate group matches?').subscribe(confirmed => {
      if (confirmed) {
        this.matchService.generateGroupMatches(this.tournamentId).subscribe({
          next: () => {
            this.loadMatches();
          },
          error: () => this.notification.showError('Error generating matches!')
        });
      }
    });
  }

  canManageEvents(match: MatchItemResponse | null): boolean {
    if (!match || !this.currentUser) {
      return false;
    }
    if (this.isAdminUser() || this.isOrganizerUser()) {
      return true;
    }
    return match.referee?.id === this.currentUser.id;
  }

  canAssignReferee(match: MatchItemResponse | null): boolean {
    return this.canManageMatches;
  }

  canDeleteMatch(match: MatchItemResponse | null): boolean {
    return this.canManageMatches;
  }

  canAccessTacticsBoard(match: MatchItemResponse | null): boolean {
    if (!match) {
      return false;
    }
    if (!this.currentUser) {
      return false;
    }
    if (this.canManageMatches || this.isAdminUser()) {
      return true;
    }
    return this.isCoachForMatch(match);
  }

  openMatchEvents(match: MatchItemResponse | null): void {
    if (!match || !match.id) {
      return;
    }
    this.router.navigate([`/tournament/${this.tournamentId}/matches/${match.id}/events`]);
  }

  openTacticsBoard(match: MatchItemResponse | null): void {
    if (!match || !match.id) {
      return;
    }
    if (!this.canAccessTacticsBoard(match)) {
      this.notification.showInfo('Only the organizer or team coaches can manage the tactics board.');
      return;
    }

    let teamId: number | null = null;
    if (!this.canManageMatches && !this.isAdminUser() && this.currentUser) {
      if (match.teamHome?.id && this.teamCoachMap[match.teamHome.id] === this.currentUser.id) {
        teamId = match.teamHome.id;
      } else if (match.teamAway?.id && this.teamCoachMap[match.teamAway.id] === this.currentUser.id) {
        teamId = match.teamAway.id;
      }
    }

    const extras = teamId !== null ? { queryParams: { teamId } } : {};
    this.router.navigate([`/tournament/${this.tournamentId}/matches/${match.id}/tactics`], extras);
  }

  onRefereeSelected(match: MatchItemResponse, refereeId: number | null): void {
    if (!match || !this.canAssignReferee(match)) {
      this.notification.showInfo('Only the organizer can assign referees.');
      this.selectedReferees[match.id] = match.referee ? match.referee.id ?? null : null;
      return;
    }

    if (refereeId === null) {
      this.selectedReferees[match.id] = match.referee ? match.referee.id ?? null : null;
      return;
    }

    this.assigning[match.id] = true;
    this.matchService.assignReferee(this.tournamentId, match.id, refereeId).subscribe({
      next: updatedMatch => {
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

  get paginatedMatches(): (MatchItemResponse | null)[] {
    const start = (this.currentPage - 1) * this.pageSize;
    const page = this.matches.slice(start, start + this.pageSize);
    const empty = this.pageSize - page.length;
    return [...page, ...Array(empty).fill(null)];
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.matches.length / this.pageSize));
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  get readOnlyView(): boolean {
    return !this.canManageMatches;
  }

  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }

  private updateManagementState(): void {
    const previous = this.canManageMatches;
    const isAdmin = this.isAdminUser();
    const isOrganizer = this.isOrganizerUser();
    this.canManageMatches = !!this.currentUser && (isAdmin || isOrganizer);

    if (this.canManageMatches && !previous) {
      this.loadReferees();
    }

    if (!this.canManageMatches) {
      this.referees = [];
    }
    this.refreshTeamCoachCache();
  }

  private isAdminUser(): boolean {
    return this.currentUser?.userRole === 'ADMIN';
  }

  private isOrganizerUser(): boolean {
    return !!this.currentUser && !!this.tournament && this.tournament.organizer?.id === this.currentUser.id;
  }

  private isCoachForMatch(match: MatchItemResponse): boolean {
    if (!this.currentUser) {
      return false;
    }
    const homeCoachId = match.teamHome?.id ? this.teamCoachMap[match.teamHome.id] : null;
    if (homeCoachId === this.currentUser.id) {
      return true;
    }
    const awayCoachId = match.teamAway?.id ? this.teamCoachMap[match.teamAway.id] : null;
    return awayCoachId === this.currentUser.id;
  }

  isAssignedReferee(match: MatchItemResponse | null): boolean {
    if (!match || !this.currentUser) {
      return false;
    }
    return match.referee?.id === this.currentUser.id;
  }

  private refreshTeamCoachCache(): void {
    if (!this.currentUser || this.canManageMatches || this.isAdminUser()) {
      return;
    }
    const teamIds = new Set<number>();
    this.matches.forEach(match => {
      if (!match) {
        return;
      }
      const homeId = match.teamHome?.id;
      const awayId = match.teamAway?.id;
      if (homeId) {
        teamIds.add(homeId);
      }
      if (awayId) {
        teamIds.add(awayId);
      }
    });

    teamIds.forEach(teamId => {
      if (this.coachRequests.has(teamId)) {
        return;
      }
      this.coachRequests.add(teamId);
      this.teamService.getTeamById(this.tournamentId, teamId).subscribe({
        next: team => {
          this.teamCoachMap[teamId] = team.coach?.id ?? null;
        },
        error: () => {
          if (!(teamId in this.teamCoachMap)) {
            this.teamCoachMap[teamId] = null;
          }
        }
      });
    });
  }
}
