import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TeamService } from '../../../services/team.service';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { TeamResponse } from '../../../models/team/team.response';
import { PlayerRef } from '../../../models/common/player-ref.model';
import { UpdatePlayerRequest } from '../../../models/team/update-player.request';
import { UpdateTeamRequest } from '../../../models/team/update-team.request';

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
export class TeamDetailsComponent {
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

  constructor(
    private route: ActivatedRoute,
    private teamService: TeamService,
    private router: Router,
    public auth: AuthService,
    private notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const tournamentId = params.get('tournamentId');
      const teamId = params.get('teamId');

      if (tournamentId && teamId) {
        this.tournamentId = +tournamentId;
        this.teamId = +teamId;
        this.loadTeamDetails();
      } else {
        this.notification.showError('Tournament or team ID not found!');
        this.router.navigate(['/tournaments/my']);
      }
    });
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

  updateTeam(): void {
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
    this.router.navigate(['/tournament', this.tournamentId, 'teams', this.teamId, 'add-player']);
  }

  deletePlayer(playerId: number): void {
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

  // ----- menu -----
  toggleMenu(playerId: number): void {
    this.openedPlayerId = (this.openedPlayerId === playerId) ? null : playerId;
  }
  isMenuOpen(playerId: number): boolean {
    return this.openedPlayerId === playerId;
  }
  closeAllMenus(): void {
    this.openedPlayerId = null;
  }

  // ----- edycja gracza -----
  startEdit(player: PlayerRef): void {
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
}
