import { Component } from '@angular/core';
import { count } from 'rxjs';
import { TeamService } from '../../../services/team.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-add-team',
  standalone: false,
  templateUrl: './add-team.component.html',
  styleUrl: './add-team.component.css'
})
export class AddTeamComponent {
  tournamentId!: number;
  form = {
    name: '',
    country: '',
    coachEmail: ''
}

constructor(private teamService: TeamService, public router: Router, private route: ActivatedRoute, public auth: AuthService, private notification: NotificationService) {}
  
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('tournamentId');
      if (id) {
        this.tournamentId = +id;
      } else {
        this.notification.showError('Tournament ID not found!');
        this.router.navigate(['/tournament', 'my']);
      }
    });
  }

  create() {
    this.teamService.createTeam(this.tournamentId, this.form).subscribe({
      next: () => {
        this.notification.showSuccess('Team created!');
        this.router.navigate(['/tournament', this.tournamentId, 'teams']);
      },
      error: () => this.notification.showError('Error while creating team')
    });
  }

    logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']); 
    });
  }
}
