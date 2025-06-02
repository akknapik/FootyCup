import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TeamService } from '../../../services/team.service';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-team-details',
  standalone: false,
  templateUrl: './team-details.component.html',
  styleUrl: './team-details.component.css'
})
export class TeamDetailsComponent {
  tournamentId!: number;
  teamId!: number;
  team: any;
  playerList: any[] = [];
  selectedPlayer: any = null;
  pageSize = 8;
  currentPage = 1;
  editMode = false;

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

  loadTeamDetails() {
    this.teamService.getTeamById(this.tournamentId, this.teamId).subscribe({
      next: (data) => {
        this.team = data;
        this.playerList = (data.playerList || []).map((player: any) => ({
          ...player,
          showMenu: false
        }));
      },
      error: () => this.notification.showError('Error loading team details!')
    });
  }

  updateTeam() {
    const payload = {
      name: this.team.name,
      country: this.team.country,
      coachEmail: this.team.coach?.email
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
      if (confirmed) {
        this.teamService.removePlayerFromTeam(this.tournamentId, this.teamId, playerId).subscribe({
          next: () => {
            this.notification.showSuccess('Player deleted!');
            this.loadTeamDetails();
          },
          error: () => this.notification.showError('Error deleting player!')
        });
      }
    });
  }

  toggleMenu(player: any): void {
    this.playerList.forEach(p => {
      if (p.id !== player.id) p.showMenu = false;
    });

    player.showMenu = !player.showMenu;
  }

  startEdit(player: any): void {
    this.selectedPlayer = { ...player };
    this.editMode = true;
    this.closeAllMenus();
  }

  closeAllMenus(): void {
    this.playerList.forEach(p => p.showMenu = false);
  }

  cancelEdit(): void {
    this.selectedPlayer = null;
    this.editMode = false;
  }

  updatePlayer(): void {
    if (this.selectedPlayer) {
      this.teamService.updatePlayerInTeam(
        this.tournamentId,
        this.teamId,
        this.selectedPlayer.id,
        this.selectedPlayer
      ).subscribe({
        next: () => {
          this.notification.showSuccess('Player updated!');
          this.loadTeamDetails();
          this.cancelEdit();
        },
        error: () => this.notification.showError('Error updating player!')
      });
    }
  }

  get totalPages(): number {
    return Math.ceil((this.playerList?.length || 0) / this.pageSize) || 1;
  }

  get paginatedPlayers(): (any | null)[] {
    const start = (this.currentPage - 1) * this.pageSize;
    const page = this.playerList.slice(start, start + this.pageSize);
    const empty = this.pageSize - page.length;
    return [...page, ...Array(empty).fill(null)];
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
