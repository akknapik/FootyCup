import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TeamService } from '../../../services/team.service';

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

  constructor(private route: ActivatedRoute, private teamService: TeamService, private router: Router) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const tournamentId = params.get('tournamentId');
      const teamId = params.get('teamId');
      if (tournamentId && teamId) {
        this.tournamentId = +tournamentId;
        this.teamId = +teamId;
        this.loadTeamDetails();
      } else {
        alert('Nie znaleziono identyfikatora turnieju lub drużyny');
        this.router.navigate(['/tournaments/my']);
      }
    });
  }

  loadTeamDetails() {
    this.teamService.getTeamById(this.tournamentId, this.teamId).subscribe({
      next: (data) => {
        this.team = data;
        this.playerList = data.playerList || [];  
      },
      error: () => alert('Błąd ładowania szczegółów drużyny')
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
        alert('Drużyna zaktualizowana!');
        this.loadTeamDetails();
      },
      error: () => alert('Błąd podczas aktualizacji drużyny')
    });
  }

  goBack(): void {
    this.router.navigate(['/tournament', this.tournamentId, 'teams']);
  }

  goToAddPlayer(): void {
    this.router.navigate(['/tournament', this.tournamentId, 'teams', this.teamId, 'add-player']);
  }

  deletePlayer(playerId: number): void {
    if (confirm('Czy na pewno chcesz usunąć tego zawodnika?')) {
      this.teamService.removePlayerFromTeam(this.tournamentId, this.teamId, playerId).subscribe({
        next: () => {
          alert('Zawodnik usunięty!');
          this.loadTeamDetails();
        },
        error: () => alert('Błąd podczas usuwania zawodnika')
      });
    }
  }

  editPlayer(player: any) {
    this.selectedPlayer = { ...player }; 
  }

  updatePlayer() {
    if (this.selectedPlayer) {
      this.teamService.updatePlayerInTeam(this.tournamentId, this.teamId, this.selectedPlayer.id, this.selectedPlayer).subscribe({
        next: () => {
          alert('Zawodnik zaktualizowany!');
          this.loadTeamDetails();
          this.selectedPlayer = null; 
        },
        error: () => alert('Błąd podczas aktualizacji zawodnika')
      });
    }
  }

  cancelEdit() {
    this.selectedPlayer = null;
  }
}
