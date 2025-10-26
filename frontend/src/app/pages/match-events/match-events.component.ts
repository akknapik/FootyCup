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
import { FormatService } from '../../services/format.service';
import { TournamentService } from '../../services/tournament.service';
import { MatchResponse } from '../../models/match/match.response';
import { MatchEventRef } from '../../models/common/match-event-ref.model';
import { CreateMatchEventRequest, MatchEventType } from '../../models/match/create-match-event.request';
import { MatchStatisticsResponse } from '../../models/match/match-statistics.response';

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
  currentUser: User | null = null;
  canManageEvents = false;

  homePlayers: PlayerRef[] = [];
  awayPlayers: PlayerRef[] = [];
  availablePlayers: PlayerRef[] = [];

  showStatistics = false;
  statistics?: MatchStatisticsResponse;
  statisticsLoading = false;
  statisticsError: string | null = null;

  private subscription?: Subscription;
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private matchService: MatchService,
    private matchEventService: MatchEventService,
    private teamService: TeamService,
    private notification: NotificationService,
    public auth: AuthService
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

    const isReferee = this.match.referee?.id === this.currentUser.id;
    const isAdmin = this.currentUser.userRole === 'ADMIN';
    this.canManageEvents = Boolean(isReferee || isAdmin);
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