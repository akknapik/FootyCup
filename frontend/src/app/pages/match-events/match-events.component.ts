import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MatchService } from '../../services/match.service';
import { MatchEventService } from '../../services/match-event.service';
import { NotificationService } from '../../services/notification.service';
import { TeamService } from '../../services/team.service';
import { PlayerRef } from '../../models/common/player-ref.model';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { TournamentService } from '../../services/tournament.service';
import { MatchResponse } from '../../models/match/match.response';
import { MatchEventRef } from '../../models/common/match-event-ref.model';
import { CreateMatchEventRequest, MatchEventType } from '../../models/match/create-match-event.request';
import { MatchStatisticsResponse } from '../../models/match/match-statistics.response';
import { HttpResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';


@Component({
  selector: 'app-match-events',
  standalone: false,
  templateUrl: './match-events.component.html',
  styleUrls: ['./match-events.component.css']
})
export class MatchEventsComponent implements OnInit, OnDestroy {
  tournamentId!: number;
  matchId!: number;
  match?: MatchResponse;
  events: MatchEventRef[] = [];
  eventTypes: MatchEventType[] = ['GOAL', 'YELLOW_CARD', 'RED_CARD', 'SUBSTITUTION', 'OTHER'];
  selectedEventType: MatchEventType = 'GOAL';
  selectedTeamId: number | null = null;
  selectedPrimaryPlayerId: number | null = null;
  selectedSecondaryPlayerId: number | null = null;
  eventMinute = 0;
  eventDescription = '';
  isSubmitting = false;
  isLoading = false;
  canManageEvents = false;
  currentUser: User | null = null;
  tournamentOrganizerId: number | null = null;

  homePlayers: PlayerRef[] = [];
  awayPlayers: PlayerRef[] = [];
  availablePlayers: PlayerRef[] = [];

  showStatistics = false;
  statistics?: MatchStatisticsResponse;
  statisticsLoading = false;
  statisticsError: string | null = null;
  isExportingMatch: Record<'pdf' | 'csv', boolean> = { pdf: false, csv: false };

  private subscription?: Subscription;
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private matchService: MatchService,
    private matchEventService: MatchEventService,
    private teamService: TeamService,
    private notification: NotificationService,
    public auth: AuthService,
    private tournamentService: TournamentService
  ) {}

  ngOnInit(): void {
    this.tournamentId = +this.route.snapshot.paramMap.get('tournamentId')!;
    this.matchId = +this.route.snapshot.paramMap.get('matchId')!;

    this.subscription = this.auth.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.updatePermissions();
    });

    this.loadMatch();
    this.loadEvents();
    this.loadTournamentContext();
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  loadMatch(): void {
    this.isLoading = true;
    this.matchService.getMatch(this.tournamentId, this.matchId).subscribe({
      next: (match) => {
        this.match = match;
        this.isLoading = false;
        this.updatePermissions();
        this.selectedTeamId = match.teamHome?.id ?? match.teamAway?.id ?? null;
        this.loadTeamPlayers(match.teamHome?.id ?? null, true);
        this.loadTeamPlayers(match.teamAway?.id ?? null, false);
        this.updateAvailablePlayers();
      },
      error: () => {
        this.isLoading = false;
        this.notification.showError('Failed to load match details.');
      }
    });
  }

  loadEvents(): void {
    this.matchEventService.getEvents(this.tournamentId, this.matchId).subscribe({
      next: events => this.events = events,
      error: () => this.notification.showError('Failed to load match events.')
    });
  }

    private loadTournamentContext(): void {
    this.tournamentService.getTournamentById(this.tournamentId).subscribe({
      next: tournament => {
        this.tournamentOrganizerId = tournament.organizer?.id ?? null;
        this.updatePermissions();
      },
      error: () => {
        this.tournamentOrganizerId = null;
        this.updatePermissions();
      }
    });
  }

  private loadTeamPlayers(teamId: number | null, isHome: boolean): void {
    if (!teamId) {
      if (isHome) {
        this.homePlayers = [];
      } else {
        this.awayPlayers = [];
      }
      this.updateAvailablePlayers();
      return;
    }

    this.teamService.getTeamById(this.tournamentId, teamId).subscribe({
      next: response => {
        if (isHome) {
          this.homePlayers = response.players ?? [];
        } else {
          this.awayPlayers = response.players ?? [];
        }
        this.updateAvailablePlayers();
      },
      error: () => this.notification.showError('Failed to load team players.')
    });
  }

  onTeamChange(teamId: number | null): void {
    this.selectedTeamId = teamId ?? null;
    this.resetPlayerSelections();
    this.updateAvailablePlayers();
  }

  private updateAvailablePlayers(): void {
    if (!this.selectedTeamId) {
      this.availablePlayers = [];
      return;
    }

    if (this.match?.teamHome?.id === this.selectedTeamId) {
      this.availablePlayers = this.homePlayers;
    } else if (this.match?.teamAway?.id === this.selectedTeamId) {
      this.availablePlayers = this.awayPlayers;
    } else {
      this.availablePlayers = [];
    }
  }

  onEventTypeChange(eventType: MatchEventType): void {
    this.selectedEventType = eventType;
    this.resetPlayerSelections();
    if (eventType !== 'OTHER') {
      this.eventDescription = '';
    }
  }

  submitEvent(): void {
    if (!this.canManageEvents) {
      this.notification.showError('You are not allowed to record events for this match.');
      return;
    }

    if (this.selectedTeamId === null) {
      this.notification.showError('Please select a team.');
      return;
    }

    if (this.eventMinute < 0) {
      this.notification.showError('Minute must be a non-negative value.');
      return;
    }

    if (this.selectedEventType === 'SUBSTITUTION') {
      if (this.selectedPrimaryPlayerId === null || this.selectedSecondaryPlayerId === null) {
        this.notification.showError('Please select both players for the substitution.');
        return;
      }
      if (this.selectedPrimaryPlayerId === this.selectedSecondaryPlayerId) {
        this.notification.showError('Substitution players must be different.');
        return;
      }
    }

    const payload: CreateMatchEventRequest = {
      eventType: this.selectedEventType,
      minute: this.eventMinute,
      teamId: this.selectedTeamId,
      playerId: this.selectedPrimaryPlayerId ?? undefined,
      secondaryPlayerId: this.selectedEventType === 'SUBSTITUTION' ? this.selectedSecondaryPlayerId ?? undefined : undefined,
      description: this.selectedEventType === 'OTHER' ? (this.eventDescription?.trim() || undefined) : undefined  
    };

    this.isSubmitting = true;
    this.matchEventService.addEvent(this.tournamentId, this.matchId, payload).subscribe({
      next: () => {
        this.notification.showSuccess('Event recorded successfully.');
        this.isSubmitting = false;
        this.resetPlayerSelections();
        if (this.selectedEventType !== 'OTHER') {
          this.eventDescription = '';
        }
        if (this.selectedEventType === 'GOAL') {
          this.eventMinute = Math.min(this.eventMinute + 1, 200);
        }
        this.loadEvents();
        this.loadMatch();
        if (this.showStatistics) {
          this.loadStatistics();
        }
      },
      error: () => {
        this.isSubmitting = false;
        this.notification.showError('Failed to record event.');
      }
    });
  }

  deleteEvent(event: MatchEventRef): void {
    this.notification.confirm('Delete this event?').subscribe(confirmed => {
      if (!confirmed) {
        return;
      }

      this.matchEventService.deleteEvent(this.tournamentId, this.matchId, event.id).subscribe({
        next: () => {
          this.notification.showSuccess('Event removed.');
          this.loadEvents();
          this.loadMatch();
          if (this.showStatistics) {
            this.loadStatistics();
          }
        },
        error: () => this.notification.showError('Failed to delete event.')
      });
    });
  }

  exportMatch(format: 'pdf' | 'csv'): void {
    if (this.isExportingMatch[format]) {
      return;
    }

    this.isExportingMatch[format] = true;
    this.matchService.exportMatch(this.tournamentId, this.matchId, format)
      .pipe(finalize(() => this.isExportingMatch[format] = false))
      .subscribe({
        next: (response) => this.handleFileDownload(response, `match-${this.matchId}.${format}`),
        error: () => this.notification.showError('Failed to export match data.')
      });
  }

  private handleFileDownload(response: HttpResponse<Blob>, fallbackName: string): void {
    const blob = response.body;
    if (!blob) {
      this.notification.showError('The export response did not contain any data.');
      return;
    }

    const filename = this.extractFilename(response.headers.get('content-disposition')) ?? fallbackName;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }

  private extractFilename(header: string | null): string | null {
    if (!header) {
      return null;
    }
    const match = /filename\*=UTF-8''([^;]+)|filename="?([^";]+)"?/i.exec(header);
    if (match) {
      return decodeURIComponent(match[1] || match[2]);
    }
    return null;
  }

  get formattedScore(): string {
    if (!this.match) {
      return '0 : 0';
    }
    const home = this.match.homeScore ?? 0;
    const away = this.match.awayScore ?? 0;
    return `${home} : ${away}`;
  }

  private updatePermissions(): void {
    if (!this.match || !this.currentUser) {
      this.canManageEvents = false;
      return;
    }

    const isAdmin = this.currentUser.userRole === 'ADMIN';
    const isOrganizer = this.tournamentOrganizerId !== null && this.currentUser.id === this.tournamentOrganizerId;
    const isReferee = this.match.referee?.id === this.currentUser.id;
    this.canManageEvents = isAdmin || isOrganizer || isReferee;
  }

  goBack(): void {
    this.router.navigate([`/tournament/${this.tournamentId}/matches`]);
  }

openStatistics(): void {
    this.showStatistics = true;
    this.statistics = undefined;
    this.loadStatistics();
  }

  closeStatistics(): void {
    this.showStatistics = false;
  }

  private loadStatistics(): void {
    this.statisticsLoading = true;
    this.statisticsError = null;
    this.matchEventService.getStatistics(this.tournamentId, this.matchId).subscribe({
      next: (stats) => {
        this.statistics = stats;
        this.statisticsLoading = false;
      },
      error: () => {
        this.statisticsLoading = false;
        this.statisticsError = 'Failed to load match statistics.';
      }
    });
  }

  private resetPlayerSelections(): void {
    this.selectedPrimaryPlayerId = null;
    this.selectedSecondaryPlayerId = null;
  }

  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}