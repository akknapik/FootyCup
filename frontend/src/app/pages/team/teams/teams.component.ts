import { Component } from '@angular/core';
import { TeamService } from '../../../services/team.service';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-teams',
  standalone: false,
  templateUrl: './teams.component.html',
  styleUrl: './teams.component.css'
})
export class TeamsComponent {
  teams: any[] = [];
  tournamentId!: number;

  constructor(private teamService: TeamService, private router: Router, private route: ActivatedRoute, public auth: AuthService, private notification: NotificationService) {}

  ngOnInit(): void {
    this.tournamentId = +this.route.snapshot.paramMap.get('tournamentId')!; 
    this.loadTeams();
  }

  loadTeams() {
    this.teamService.getTeams(this.tournamentId).subscribe({
      next: (data) => this.teams = data,
      error: () => this.notification.showError('Error loading teams')
    });
  }

  goToCreate() {
    this.router.navigate(['/tournament', this.tournamentId, 'teams', 'new']);
  }

  deleteTeam(teamId: number) {
    this.notification.confirm('Are you sure you want to delete this team?')
      .subscribe(confirmed => {
        if (confirmed) {
          this.teamService.deleteTeam(this.tournamentId, teamId).subscribe({
            next: () => {
              this.notification.showSuccess('Team deleted!');
              this.loadTeams();
            },
            error: () => this.notification.showError('Error deleting team')
          });
       }
      }
    );
  }

  openDetails(id: number): void {
    this.router.navigate(['/tournament', this.tournamentId, 'teams', id]);
  }
}
