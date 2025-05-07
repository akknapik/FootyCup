import { Component } from '@angular/core';
import { TournamentService } from '../../../services/tournament.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-add-tournament',
  standalone: false,
  templateUrl: './add-tournament.component.html',
  styleUrl: './add-tournament.component.css'
})
export class AddTournamentComponent {
  form = {
    name: '',
    startDate: '',
    endDate: '',
    location: '',
  };

  constructor(private tournamentService: TournamentService, public router: Router, public auth: AuthService, private notification: NotificationService) {}

  create() {
    this.tournamentService.createTournament(this.form).subscribe({
      next: () => {
        this.notification.showSuccess('Tournament created'),
        this.router.navigate(['/tournaments/my']);
      },
      error: () => this.notification.showError('Error while creating tournament')
    });
  }
}
