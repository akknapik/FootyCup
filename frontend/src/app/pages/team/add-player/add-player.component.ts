import { Component } from '@angular/core';
import { TeamService } from '../../../services/team.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

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
    number: '',
    name: '',
    birthDate: ''
  }

  constructor(private teamService: TeamService, private router: Router, private route: ActivatedRoute, public auth: AuthService) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('tournamentId');
      const teamId = params.get('teamId');
      if (id && teamId) {
        this.tournamentId = +id;
        this.teamId = +teamId;
      } else {
        alert('Nie znaleziono identyfikatora turnieju lub drużyny');
        this.router.navigate(['/tournament', 'my']);
      }
    });
  }

  create() {
    this.teamService.addPlayerToTeam(this.tournamentId, this.teamId, this.form).subscribe({
      next: () => {
        alert('Zawodnik utworzony!');
        this.router.navigate(['/tournament', this.tournamentId, 'teams', this.teamId]);
      },
      error: () => alert('Błąd podczas tworzenia zawodnika')
    });
  }

  cancel() {
    this.router.navigate(['/tournament', this.tournamentId, 'teams', this.teamId]);
  }
}
