import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ScheduleEntry } from '../../models/schedule-entry.model';
import { Schedule } from '../../models/schedule.model';
import { MatchService } from '../../services/match.service';
import { ScheduleService } from '../../services/schedule.service';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { ResultService } from '../../services/result.service';
import { BracketNode } from '../../models/bracket-node.model';
import { Group } from '../../models/group.model';
import { GroupTeam } from '../../models/group-team.model';
import { MatchResponse } from '../../models/match/match.response';
import { ScheduleResponse } from '../../models/schedule/schedule.response';
import { ScheduleEntryResponse } from '../../models/schedule/schedule-entry.response';
import { ScheduleListItemResponse } from '../../models/schedule/schedule-list-item.response';
import { GroupResponse } from '../../models/format/group/group.response';
import { BracketNodeResponse } from '../../models/format/bracket/bracket-node.response';
import { MatchRef } from '../../models/common/match-ref.model';
import { Subscription } from 'rxjs';
import { TournamentResponse } from '../../models/tournament/tournament.response';
import { User } from '../../models/user.model';
import { TournamentService } from '../../services/tournament.service';


@Component({
  selector: 'app-result',
  standalone: false,
  templateUrl: './result.component.html',
  styleUrl: './result.component.css'
})
export class ResultComponent implements OnInit, OnDestroy {
  tournamentId!: number;
  schedules: ScheduleListItemResponse[] = [];
  selectedSchedule!: ScheduleResponse;
  selectedScheduleId!: number;
  scheduleEntries: ScheduleEntryResponse[] = [];
  activeTab: string = 'schedule';
  groups: GroupResponse[] = [];
  // groupTeams: GroupTeam[] = [];
  bracket: BracketNodeResponse[] = [];
  isLoadingSchedule: boolean = false;
  isLoadingGroups: boolean = false;
  isLoadingBracket: boolean = false;
  tournament: TournamentResponse | null = null;
  currentUser: User | null = null;
  canEditResults = false;

  private userSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private scheduleService: ScheduleService,
    public auth: AuthService,
    private notification: NotificationService,
    private resultService: ResultService,
    private tournamentService: TournamentService
   ) {}

  ngOnInit(): void {
      this.userSubscription = this.auth.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.updatePermissions();
    });

    this.route.paramMap.subscribe(pm => {
      this.tournamentId = +pm.get('tournamentId')!;
      this.loadTournament();
      this.loadSchedulesList();
    });
  }

  ngOnDestroy(): void {
    this.userSubscription?.unsubscribe();
  }

  loadSchedulesList(): void {
  this.isLoadingSchedule = true;
  this.scheduleService.getSchedulesList(this.tournamentId).subscribe({
    next: (list) => {
      this.schedules = list.sort((a, b) =>
        new Date(a.startDateTime!).getTime() - new Date(b.startDateTime!).getTime()
      );
      if (this.schedules.length) {
        this.selectSchedule(this.schedules[0].id!);
      } else {
        this.isLoadingSchedule = false;
      }
    },
    error: () => {
      this.notification.showError('Error loading schedules');
      this.isLoadingSchedule = false;
    }
  });
}

selectSchedule(scheduleId: number): void {
  this.isLoadingSchedule = true;
  this.selectedScheduleId = scheduleId;
  this.scheduleService
    .getScheduleById(this.tournamentId, scheduleId)
    .subscribe({
      next: (sched) => {
        this.selectedSchedule = sched;
        this.scheduleEntries = sched.entries;
        this.isLoadingSchedule = false;
      },
      error: () => {
        this.notification.showError('Error loading schedule details');
        this.isLoadingSchedule = false;
      }
    });
}

onResultChange(match: MatchRef | MatchResponse | null): void {
  if (!match || !this.canEditResults) return;

  const id = match.id!;
  const isFull = (m: any): m is MatchResponse => 'durationInMin' in m;

  const homeScore = isFull(match) ? (match.homeScore ?? 0) : (match.homeScore ?? 0);
  const awayScore = isFull(match) ? (match.awayScore ?? 0) : (match.awayScore ?? 0);

  this.resultService.updateMatchResult(this.tournamentId, {
    matchId: id,
    homeScore,
    awayScore
  }).subscribe({
    next: () => this.notification.showSuccess('Result saved!'),
    error: () => this.notification.showError('Error saving result')
  });
}

  private loadTournament(): void {
    this.tournamentService.getTournamentById(this.tournamentId).subscribe({
      next: tournament => {
        this.tournament = tournament;
        this.updatePermissions();
      },
      error: err => {
        this.tournament = null;
        this.updatePermissions();
        if (err.status === 403) {
          this.notification.showInfo('You can view results, but only the organizer can update them.');
        } else {
          this.notification.showError('Error loading tournament details');
        }
      }
    });
  }

  private updatePermissions(): void {
    this.canEditResults = !!this.currentUser && !!this.tournament &&
      this.tournament.organizer?.id === this.currentUser.id;
  }

loadGroups(): void {
  this.isLoadingGroups = true;
  this.resultService.getGroups(this.tournamentId).subscribe({
    next: (g) => {
      this.groups = g;
      this.isLoadingGroups = false;
    },
    error: () => {
      this.notification.showError('Error loading groups');
      this.isLoadingGroups = false;
    }
  });
}

loadBracket(): void {
  this.isLoadingBracket = true;
  this.resultService.getBracket(this.tournamentId).subscribe({
    next: (b) => {
      this.bracket = b;
      this.isLoadingBracket = false;
    },
    error: () => {
      this.notification.showError('Error loading bracket');
      this.isLoadingBracket = false;
    }
  });
}

  onTabChange(tab: string): void {
  this.activeTab = tab;

  if (tab === 'groups') {
    this.loadGroups();
  } else if (tab === 'bracket') {
    this.loadBracket();
  }
}

  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']); 
    });
  }

}

