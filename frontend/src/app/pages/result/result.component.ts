import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ScheduleEntry } from '../../models/schedule-entry.model';
import { Schedule } from '../../models/schedule.model';
import { MatchService } from '../../services/match.service';
import { ScheduleService } from '../../services/schedule.service';
import { Match } from '../../models/match.model';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { ResultService } from '../../services/result.service';
import { BracketNode } from '../../models/bracket-node.model';
import { Group } from '../../models/group.model';
import { GroupTeam } from '../../models/group-team.model';


@Component({
  selector: 'app-result',
  standalone: false,
  templateUrl: './result.component.html',
  styleUrl: './result.component.css'
})
export class ResultComponent implements OnInit {
  tournamentId!: number;
  schedules: Schedule[] = [];
  selectedSchedule!: Schedule;
  selectedScheduleId!: number;
  scheduleEntries: ScheduleEntry[] = [];
  activeTab: string = 'schedule';
  groups: Group[] = [];
  groupTeams: GroupTeam[] = [];
  bracket: BracketNode[] = [];
  isLoadingSchedule: boolean = false;
  isLoadingGroups: boolean = false;
  isLoadingBracket: boolean = false;

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private scheduleService: ScheduleService,
    private matchService: MatchService,
    public auth: AuthService,
    private notification: NotificationService, 
    private resultService: ResultService 
   ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(pm => {
      this.tournamentId = +pm.get('tournamentId')!;
      this.loadSchedulesList();
    });
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

  onResultChange(match: Match | null): void {
  if (!match) return;

  this.resultService.updateMatchResult(this.tournamentId, {
    id: match.id!,
    homeScore: match.homeScore ?? 0,
    awayScore: match.awayScore ?? 0
  }).subscribe({
      next: () => this.notification.showSuccess('Result saved!'),
      error: () => this.notification.showError('Error saving result')
    });
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

