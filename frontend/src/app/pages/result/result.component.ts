import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
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

  constructor(
    private route: ActivatedRoute,
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
    this.scheduleService.getSchedulesList(this.tournamentId).subscribe(list => {
      this.schedules = list.sort((a, b) =>
        new Date(a.startDateTime!).getTime() - new Date(b.startDateTime!).getTime()
      );
      if (this.schedules.length) {
        this.selectSchedule(this.schedules[0].id!);
      }
    });
  }

  selectSchedule(scheduleId: number): void {
    this.selectedScheduleId = scheduleId;
    this.scheduleService
      .getScheduleById(this.tournamentId, scheduleId)
      .subscribe(sched => {
        this.selectedSchedule = sched;
        this.scheduleEntries = sched.entries;
      });
  }

  onResultChange(match: Match | null): void {
  if (!match) return;

  this.resultService.updateMatchResult(this.tournamentId, {
    id: match.id!,
    homeScore: match.homeScore ?? 0,
    awayScore: match.awayScore ?? 0
  }).subscribe({
      next: () => this.notification.showSuccess('Wynik zapisany!'),
      error: () => this.notification.showError('Błąd zapisu wyniku')
    });
  }

  loadGroups(): void {
    this.resultService.getGroups(this.tournamentId).subscribe(g => this.groups = g);
  }

  loadBracket(): void {
    this.resultService.getBracket(this.tournamentId).subscribe(b => this.bracket = b);
  }

  onTabChange(tab: string): void {
  this.activeTab = tab;

  if (tab === 'groups') {
    this.loadGroups();
  } else if (tab === 'bracket') {
    this.loadBracket();
  }
}

}

