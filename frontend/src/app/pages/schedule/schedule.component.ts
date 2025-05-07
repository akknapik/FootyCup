import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  CdkDragDrop,
  moveItemInArray,
  transferArrayItem
} from '@angular/cdk/drag-drop';
import { ScheduleService } from '../../services/schedule.service';
import { Schedule } from '../../models/schedule.model';
import { ScheduleEntry } from '../../models/schedule-entry.model';
import { Match } from '../../models/match.model';
import { forkJoin } from 'rxjs';
import { MatchService } from '../../services/match.service';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-match',
  standalone: false,
  templateUrl: './schedule.component.html',
  styleUrl: './schedule.component.css'
})
export class ScheduleComponent implements OnInit {
  allMatches: Match[] = [];
  scheduleEntries: ScheduleEntry[] = [];
  scheduleId!: number;
  tournamentId!: number;
  schedule!: Schedule;
  unassigned: ScheduleEntry[] = [];

  constructor(
    private route: ActivatedRoute,
    private scheduleService: ScheduleService,
    private matchService: MatchService,
    public auth: AuthService, 
    private notification: NotificationService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(pm => {
      this.tournamentId = +pm.get('tournamentId')!;
      this.loadSchedule();
    });
  }

  private loadSchedule(): void {
    forkJoin({
      sched: this.scheduleService.getSchedule(this.tournamentId),
      matches: this.matchService.getMatches(this.tournamentId)
    }).subscribe(({ sched, matches }) => {
      this.scheduleId = sched.id;
      this.scheduleEntries = sched.entries;
  
      const scheduledIds = new Set(
        sched.entries
          .filter(e => e.type === 'MATCH' && e.match)
          .map(e => e.match!.id)
      );
  
      this.allMatches = matches.filter(m => !scheduledIds.has(m.id));
    });
  }

  onCreateSchedule(startIso: string): void {
    this.scheduleService
      .createSchedule(this.tournamentId, startIso)
      .subscribe(() => this.loadSchedule());
  }

  onAddBreak(durationInMin: number): void {
    this.scheduleService
      .addBreak(this.tournamentId, this.scheduleId, durationInMin)
      .subscribe(() => this.loadSchedule());
  }

  onRecompute(): void {
    this.scheduleService
      .recompute(this.tournamentId, this.scheduleId)
      .subscribe(() => this.loadSchedule());
  }

  drop(event: CdkDragDrop<any, any>): void {
    const prevData = event.previousContainer.data;
    const currData = event.container.data;
  
    if (prevData === this.scheduleEntries && currData === this.scheduleEntries) {
      moveItemInArray(this.scheduleEntries, event.previousIndex, event.currentIndex);
      const ids = this.scheduleEntries.map(e => e.id);
      this.scheduleService.reorderEntries(this.tournamentId, this.scheduleId, ids)
        .subscribe(() => this.loadSchedule());
    }
    else if (prevData === this.allMatches && currData === this.scheduleEntries) {
      const match = this.allMatches[event.previousIndex];
      this.scheduleService.addMatchToSchedule(this.tournamentId, this.scheduleId, match.id)
        .subscribe(() => this.loadSchedule());
    }
    else if (prevData === this.scheduleEntries && currData === this.allMatches) {
      const entry = this.scheduleEntries[event.previousIndex];
      this.scheduleService.removeEntryFromSchedule(this.tournamentId, this.scheduleId, entry.id)
        .subscribe(() => this.loadSchedule());
    }
  }

  updateTime(entry: ScheduleEntry, time: string): void {
    const [datePart] = entry.startDateTime!.split('T');
    const newStartIso = `${datePart}T${time}:00`;
    this.scheduleService
      .updateEntryTime(
        this.tournamentId,
        this.scheduleId,
        entry.id,
        newStartIso
      )
      .subscribe(() => {
        entry.startDateTime = newStartIso;
      });
  }
  addMatchToSchedule(matchId: number): void {
    this.scheduleService
      .addMatchToSchedule(this.tournamentId, this.scheduleId, matchId)
      .subscribe(() => this.loadSchedule());
  }
}
