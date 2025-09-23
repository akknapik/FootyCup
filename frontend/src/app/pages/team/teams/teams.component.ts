import { Component, HostListener } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TeamService } from '../../../services/team.service';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { TeamItemResponse } from '../../../models/team/team-item.response';

@Component({
  selector: 'app-teams',
  standalone: false,
  templateUrl: './teams.component.html',
  styleUrl: './teams.component.css'
})
export class TeamsComponent {
  teams: TeamItemResponse[] = [];
  tournamentId!: number;

  pageSize = 10;
  currentPage = 1;
  isLoading = false;

  openedMenuId: number | null = null;

  constructor(
    private teamService: TeamService,
    private router: Router,
    private route: ActivatedRoute,
    public auth: AuthService,
    private notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const tid = params.get('tournamentId');
      if (tid) {
        this.tournamentId = +tid;
        this.loadTeams();
      } else {
        this.notification.showError('Tournament ID not found');
        this.router.navigate(['/tournaments/my']);
      }
    });
  }

  loadTeams(): void {
    this.isLoading = true;
    this.teamService.getTeams(this.tournamentId).subscribe({
      next: (data) => {
        this.teams = data ?? [];
        this.clampPage();
        this.isLoading = false;
        this.closeAllMenus();
      },
      error: () => {
        this.notification.showError('Error loading teams');
        this.isLoading = false;
      }
    });
  }

  goToCreate(): void {
    this.router.navigate(['/tournament', this.tournamentId, 'teams', 'new']);
  }

  openDetails(id: number): void {
    this.router.navigate(['/tournament', this.tournamentId, 'teams', id]);
  }

  deleteTeam(teamId: number): void {
    this.notification.confirm('Are you sure you want to delete this team?')
      .subscribe(confirmed => {
        if (!confirmed) return;
        this.teamService.deleteTeam(this.tournamentId, teamId).subscribe({
          next: () => {
            this.notification.showSuccess('Team deleted!');
            this.loadTeams();
          },
          error: () => this.notification.showError('Error deleting team')
        });
      });
  }

  toggleMenu(teamId: number): void {
    this.openedMenuId = (this.openedMenuId === teamId) ? null : teamId;
  }

  isMenuOpen(teamId: number): boolean {
    return this.openedMenuId === teamId;
  }

  @HostListener('document:click')
  closeAllMenus(): void {
    this.openedMenuId = null;
  }

  // ----- paginacja -----
  get totalPages(): number {
    const n = this.teams.length;
    return n ? Math.ceil(n / this.pageSize) : 1;
  }

  get paginatedTeams(): TeamItemResponse[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.teams.slice(start, start + this.pageSize);
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.closeAllMenus();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.closeAllMenus();
    }
  }

  private clampPage(): void {
    this.currentPage = Math.min(this.currentPage, this.totalPages);
    if (this.currentPage < 1) this.currentPage = 1;
  }

  // ----- auth -----
  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}
