import { Component } from '@angular/core';
import { count } from 'rxjs';
import { TeamService } from '../../../services/team.service';
import { ActivatedRoute, Router } from '@angular/router';

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

constructor(private teamService: TeamService, private router: Router, private route: ActivatedRoute) {}
  
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('tournamentId');
      if (id) {
        this.tournamentId = +id;
      } else {
        alert('Nie znaleziono identyfikatora turnieju');
        this.router.navigate(['/tournament', 'my']);
      }
    });
  }

  create() {
    this.teamService.createTeam(this.tournamentId, this.form).subscribe({
      next: () => {
        alert('Drużyna utworzona!');
        this.router.navigate(['/tournament', this.tournamentId, 'teams']);
      },
      error: () => alert('Błąd podczas tworzenia drużyny')
    });
  }
}
