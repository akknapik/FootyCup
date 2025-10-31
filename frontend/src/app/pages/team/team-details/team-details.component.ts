import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TeamService } from '../../../services/team.service';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { TeamResponse } from '../../../models/team/team.response';
import { PlayerRef } from '../../../models/common/player-ref.model';
import { UpdatePlayerRequest } from '../../../models/team/update-player.request';
import { UpdateTeamRequest } from '../../../models/team/update-team.request';
import { TeamStatisticsResponse } from '../../../models/team/team-statistics.response';
import { PlayerStatisticsResponse } from '../../../models/team/player-statistics.response';
import { User } from '../../../models/user.model';
import { Subject, takeUntil } from 'rxjs';
import { TournamentService } from '../../../services/tournament.service';
import { TournamentResponse } from '../../../models/tournament/tournament.response';

interface EditablePlayer {
  id: number;
  name: string;
  number: number;
  birthDate?: string; // 'YYYY-MM-DD'
}

@Component({
  selector: 'app-team-details',
  standalone: false,
  templateUrl: './team-details.component.html',
  styleUrl: './team-details.component.css'
})
export class TeamDetailsComponent implements OnInit, OnDestroy {
  tournamentId!: number;
  teamId!: number;

  team: TeamResponse | null = null;
  playerList: PlayerRef[] = [];

  // edycja gracza
  selectedPlayer: EditablePlayer | null = null;
  editMode = false;

  // menu kontekstowe po id gracza
  openedPlayerId: number | null = null;

  // paginacja / loading
  pageSize = 8;
  currentPage = 1;
  isLoading = false;

  showPlayerStatsModal = false;
  playerStatsLoading = false;
  playerStatsError: string | null = null;
  playerStatistics: PlayerStatisticsResponse | null = null;
  currentStatsPlayerName: string | null = null;

  showTeamStatsModal = false;
  teamStatsLoading = false;
  teamStatsError: string | null = null;
  teamStatistics: TeamStatisticsResponse | null = null;

  currentUser: User | null = null;
  private tournamentOrganizerId: number | null = null;

  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private teamService: TeamService,
    private router: Router,
    public auth: AuthService,
    private notification: NotificationService,
    private tournamentService: TournamentService
    ) {}

  ngOnInit(): void {
    this.auth.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => this.currentUser = user);

    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
      const tournamentId = params.get('tournamentId');
      const teamId = params.get('teamId');

      if (tournamentId && teamId) {
        this.tournamentId = +tournamentId;
        this.teamId = +teamId;
        this.loadTournamentContext();
        this.loadTeamDetails();
      } else {
        this.notification.showError('Tournament or team ID not found!');
        this.router.navigate(['/tournaments/my']);
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadTeamDetails(): void {
    this.isLoading = true;
    this.teamService.getTeamById(this.tournamentId, this.teamId).subscribe({
      next: (data) => {
        this.team = data;
        this.playerList = data.players ?? [];
        this.isLoading = false;
        this.clampPage();
        this.closeAllMenus();
      },
      error: () => {
        this.notification.showError('Error loading team details!');
        this.isLoading = false;
      }
    });
  }

  private loadTournamentContext(): void {
    this.tournamentService.getTournamentById(this.tournamentId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (tournament: TournamentResponse) => {
          this.tournamentOrganizerId = tournament.organizer?.id ?? null;
        },
        error: () => {
          this.tournamentOrganizerId = null;
        }
      });
  }

  updateTeam(): void {
    if (!this.canManageTeam || !this.team) {
      if (!this.canManageTeam) {
        this.notification.showInfo('Only the organizer can update team details.');
      }
      return;
    }
    if (!this.team) return;

    const payload: UpdateTeamRequest = {
      name: this.team.name,
      country: this.team.country ?? undefined,
      coachEmail: this.team.coach?.email ?? undefined
    };

    this.teamService.updateTeam(this.tournamentId, this.teamId, payload).subscribe({
      next: () => {
        this.notification.showSuccess('Team updated!');
        this.loadTeamDetails();
      },
      error: () => this.notification.showError('Error updating team!')
    });
  }

  goBack(): void {
    this.router.navigate(['/tournament', this.tournamentId, 'teams']);
  }

  goToAddPlayer(): void {
    if (!this.canManagePlayers) {
      this.notification.showInfo('You can only view players for this team.');
      return;
    }
    this.router.navigate(['/tournament', this.tournamentId, 'teams', this.teamId, 'add-player']);
  }

  deletePlayer(playerId: number): void {
    if (!this.canManagePlayers) {
      this.notification.showInfo('Only the organizer or the team coach can manage players.');
      return;
    }
    this.notification.confirm('Are you sure you want to delete this player?').subscribe(confirmed => {
      if (!confirmed) return;

      this.teamService.removePlayerFromTeam(this.tournamentId, this.teamId, playerId).subscribe({
        next: () => {
          this.notification.showSuccess('Player deleted!');
          this.playerList = this.playerList.filter(p => p.id !== playerId);
          this.clampPage();
          this.closeAllMenus();
        },
        error: () => this.notification.showError('Error deleting player!')
      });
    });
  }

  toggleMenu(playerId: number): void {
    if (!this.canManagePlayers) {
      return;
    }
    this.openedPlayerId = (this.openedPlayerId === playerId) ? null : playerId;
  }

  isMenuOpen(playerId: number): boolean {
    if (!this.canManagePlayers) {
      return false;
    }
    return this.openedPlayerId === playerId;
  }
  closeAllMenus(): void {
    this.openedPlayerId = null;
}

openPlayerStatistics(player: PlayerRef): void {
    if (!player?.id) {
      return;
    }

    this.closeAllMenus();
    this.showPlayerStatsModal = true;
    this.playerStatsLoading = true;
    this.playerStatsError = null;
    this.playerStatistics = null;
    this.currentStatsPlayerName = player.name || 'Player';

    this.teamService.getPlayerStatistics(this.tournamentId, this.teamId, player.id).subscribe({
      next: (stats) => {
        this.playerStatistics = stats;
        this.playerStatsLoading = false;
        if (!this.currentStatsPlayerName && stats?.playerName) {
          this.currentStatsPlayerName = stats.playerName;
        }
      },
      error: () => {
        this.playerStatsError = 'Failed to load player statistics.';
        this.playerStatsLoading = false;
        this.notification.showError('Failed to load player statistics.');
      }
    });
  }

  closePlayerStatistics(): void {
    this.showPlayerStatsModal = false;
    this.playerStatsLoading = false;
    this.playerStatsError = null;
    this.playerStatistics = null;
    this.currentStatsPlayerName = null;
  }

  openTeamStatistics(): void {
    if (!this.team) {
      return;
    }

    this.showTeamStatsModal = true;
    this.teamStatsLoading = true;
    this.teamStatsError = null;
    this.teamStatistics = null;

    this.teamService.getTeamStatistics(this.tournamentId, this.teamId).subscribe({
      next: (stats) => {
        this.teamStatistics = stats;
        this.teamStatsLoading = false;
      },
      error: () => {
        this.teamStatsError = 'Failed to load team statistics.';
        this.teamStatsLoading = false;
        this.notification.showError('Failed to load team statistics.');
      }
    });
  }

  closeTeamStatistics(): void {
    this.showTeamStatsModal = false;
    this.teamStatsLoading = false;
    this.teamStatsError = null;
    this.teamStatistics = null;
  }

  startEdit(player: PlayerRef): void {
    if (!this.canManagePlayers) {
      return;
    }

    this.selectedPlayer = {
      id: player.id,
      name: player.name,
      number: player.number,
      birthDate: (player as any).birthDate
    };
    this.editMode = true;
    this.closeAllMenus();
  }

  cancelEdit(): void {
    this.selectedPlayer = null;
    this.editMode = false;
  }

  updatePlayer(): void {
    if (!this.canManagePlayers || !this.selectedPlayer) {
      if (!this.canManagePlayers) {
        this.notification.showInfo('Only the organizer or the team coach can update players.');
      }
      return;
    }

    if (!this.selectedPlayer) return;

    const body: UpdatePlayerRequest = {
      name: this.selectedPlayer.name,
      number: this.selectedPlayer.number,
      birthDate: this.selectedPlayer.birthDate || undefined
    };

    this.teamService.updatePlayerInTeam(
      this.tournamentId,
      this.teamId,
      this.selectedPlayer.id,
      body
    ).subscribe({
      next: () => {
        this.notification.showSuccess('Player updated!');
        this.loadTeamDetails();
        this.cancelEdit();
      },
      error: () => this.notification.showError('Error updating player!')
    });
  }

  get totalPages(): number {
    return Math.ceil((this.playerList?.length || 0) / this.pageSize) || 1;
  }
  get paginatedPlayers(): PlayerRef[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.playerList.slice(start, start + this.pageSize);
  }
  prevPage(): void {
    if (this.currentPage > 1) this.currentPage--;
  }
  nextPage(): void {
    if (this.currentPage < this.totalPages) this.currentPage++;
  }
  private clampPage(): void {
    this.currentPage = Math.min(this.currentPage, this.totalPages);
    if (this.currentPage < 1) this.currentPage = 1;
  }

  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }

  get canManageTeam(): boolean {
    if (this.currentUser?.userRole === 'ADMIN') {
      return true;
    }
    if (!this.currentUser || this.tournamentOrganizerId === null) {
      return false;
    }
    return this.currentUser.id === this.tournamentOrganizerId;
  }

  get canManagePlayers(): boolean {
    if (this.canManageTeam) {
      return true;
    }
    if (!this.currentUser || !this.team?.coach) {
      return false;
    }
    return this.team.coach.id === this.currentUser.id;
  }
}
