import { Component } from '@angular/core';
import { TeamService } from '../../../services/team.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-add-player',
  standalone: false,
  templateUrl: './add-player.component.html',
  styleUrl: './add-player.component.css'
})
export class AddPlayerComponent {
  tournamentId!: number;
  teamId!: number;
  form = {
    number: 0,
    name: '',
    birthDate: ''
  }

  constructor(private teamService: TeamService, private router: Router, private route: ActivatedRoute, public auth: AuthService, private notification: NotificationService) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('tournamentId');
      const teamId = params.get('teamId');
      if (id && teamId) {
        this.tournamentId = +id;
        this.teamId = +teamId;
      } else {
        this.notification.showError('Tournament or team ID not found!');
        this.router.navigate(['/tournament', 'my']);
      }
    });
  }

  create() {
    this.teamService.addPlayerToTeam(this.tournamentId, this.teamId, this.form).subscribe({
      next: () => {
        this.notification.showSuccess('Player created!');
        this.router.navigate(['/tournament', this.tournamentId, 'teams', this.teamId]);
      },
      error: () => this.notification.showError('Error while creating player!')
    });
  }

  cancel() {
    this.router.navigate(['/tournament', this.tournamentId, 'teams', this.teamId]);
  }

    logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']); 
    });
  }
}
