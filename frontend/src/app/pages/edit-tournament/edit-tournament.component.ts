import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TournamentService } from '../../services/tournament.service';

@Component({
  selector: 'app-edit-tournament',
  standalone: false,
  templateUrl: './edit-tournament.component.html',
  styleUrl: './edit-tournament.component.css'
})
export class EditTournamentComponent {
  tournamentId!: number;
  form = {
    name: '',
    startDate: '',
    endDate: '',
    location: '',
    organizer: undefined,
    status: undefined,
    system: undefined,
    id: undefined
  };

  constructor(
    private route: ActivatedRoute,
    private tournamentService: TournamentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.tournamentId = +this.route.snapshot.paramMap.get('id')!;
    this.tournamentService.getTournamentById(this.tournamentId).subscribe({
      next: (data) => this.form = { ...this.form, ...data },
      error: () => alert('Błąd ładowania danych turnieju')
    });
  }

  save() {
    const { name, startDate, endDate, location } = this.form;

    this.tournamentService.updateTournament(this.tournamentId, {
      name, startDate, endDate, location
    }).subscribe({
      next: () => {
        alert('Zapisano!');
        this.router.navigate(['/tournaments/my']);
      },
      error: () => alert('Błąd zapisu')
    });
  }
}
