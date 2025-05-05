import { Component } from '@angular/core';
import { TeamService } from '../../../services/team.service';
import { Route, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-teams',
  standalone: false,
  templateUrl: './teams.component.html',
  styleUrl: './teams.component.css'
})
export class TeamsComponent {
  teams: any[] = [];
  tournamentId!: number;

  constructor(private teamService: TeamService, private router: Router, public auth: AuthService) {}

  ngOnInit(): void {
    this.tournamentId = +this.router.url.split('/')[2]; 
    this.loadTeams();
  }

  loadTeams() {
    this.teamService.getTeams(this.tournamentId).subscribe({
      next: (data) => this.teams = data,
      error: () => alert('Błąd ładowania drużyn')
    });
  }

  goToCreate() {
    this.router.navigate(['/tournament', this.tournamentId, 'teams', 'new']);
  }

  deleteTeam(teamId: number) {
    if (confirm('Czy na pewno chcesz usunąć tę drużynę?')) {
      this.teamService.deleteTeam(this.tournamentId, teamId).subscribe({
        next: () => {
          alert('Drużyna usunięta!');
          this.loadTeams();
        },
        error: () => alert('Błąd podczas usuwania drużyny')
      });
    }
  }

  openDetails(id: number): void {
    this.router.navigate(['/tournament', this.tournamentId, 'teams', id]);
  }
}
