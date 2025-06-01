import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { forkJoin } from 'rxjs';
import { ScheduleService } from '../../services/schedule.service';
import { MatchService } from '../../services/match.service';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { Match } from '../../models/match.model';
import { ScheduleEntry } from '../../models/schedule-entry.model';
import { Schedule } from '../../models/schedule.model';

@Component({
  selector: 'app-schedule',
  standalone: false,
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css']
})
export class ScheduleComponent implements OnInit {
  tournamentId!: number;
  schedules: { id: number; startDateTime: Date }[] = [];
  selectedSchedule!: Schedule;  
  selectedScheduleId!: number;
  allMatches: Match[] = [];
  scheduleEntries: ScheduleEntry[] = [];
  breakDuration: number = 15;


  constructor(
    private route: ActivatedRoute,
    private scheduleService: ScheduleService,
    private matchService: MatchService,
    public auth: AuthService,
    private notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(pm => {
      this.tournamentId = +pm.get('tournamentId')!;
      this.loadSchedulesList();
    });
  }

  loadSchedulesList(): void {
  this.scheduleService.getSchedulesList(this.tournamentId).subscribe(list => {
    this.schedules = list
      .map(s => ({
        id: s.id!,
        startDateTime: new Date(s.startDateTime!)
      }))
      .sort((a, b) => a.startDateTime.getTime() - b.startDateTime.getTime()); 

    if (this.schedules.length) {
      this.selectSchedule(this.schedules[0].id);
    }
  });
}

  selectSchedule(id: number): void {
    this.selectedScheduleId = id;
    forkJoin({
      sched: this.scheduleService.getScheduleById(this.tournamentId, id),
      matches: this.matchService.getMatches(this.tournamentId),
      usedIds: this.scheduleService.getUsedMatchIds(this.tournamentId)
    }).subscribe(({ sched, matches, usedIds }) => {
      this.scheduleEntries = sched.entries;
      sched.startDateTime = new Date(sched.startDateTime);
      this.selectedSchedule = sched; 
      const usedIdSet = new Set(usedIds);
      this.allMatches = matches.filter(m => !usedIdSet.has(m.id));
    });
}

  onAddBreak(durationInMin: number): void {
    this.scheduleService
      .addBreak(this.tournamentId, this.selectedScheduleId, durationInMin)
      .subscribe(() => this.selectSchedule(this.selectedScheduleId));
  }

  onRecompute(): void {
    this.scheduleService
      .recompute(this.tournamentId, this.selectedScheduleId)
      .subscribe(() => this.selectSchedule(this.selectedScheduleId));
  }

drop(event: CdkDragDrop<any[]>): void {
  if (event.previousContainer === event.container) {
    moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    this.recomputeTimesAndSave();
  } else if (event.previousContainer.id === 'allMatchesList') {
    const match = this.allMatches[event.previousIndex];

    this.scheduleService
      .addMatchToSchedule(this.tournamentId, this.selectedScheduleId, match.id)
      .subscribe(() => {
        this.scheduleService.getScheduleById(this.tournamentId, this.selectedScheduleId)
          .subscribe(schedule => {
            this.selectedSchedule = schedule;
            const addedEntry = schedule.entries.find(e => e.match?.id === match.id);
            if (!addedEntry) return;

            this.allMatches.splice(event.previousIndex, 1);
            this.scheduleEntries.splice(event.currentIndex, 0, addedEntry);

            const ids = this.scheduleEntries.map(e => e.id);
            this.scheduleService
              .reorderEntries(this.tournamentId, this.selectedScheduleId, ids)
              .subscribe(() => {
                this.scheduleService
                  .recompute(this.tournamentId, this.selectedScheduleId)
                  .subscribe(() => this.selectSchedule(this.selectedScheduleId));
              });
          });
      });
  } else {
    const removed = this.scheduleEntries.splice(event.previousIndex, 1)[0];
    if (removed.type === 'MATCH' && removed.match) {
      this.allMatches.push(removed.match);
    }

    this.scheduleService
      .removeEntryFromSchedule(this.tournamentId, this.selectedScheduleId, removed.id)
      .subscribe(() => this.selectSchedule(this.selectedScheduleId));
  }
}



  updateTime(entry: ScheduleEntry, time: string): void {
    const [datePart] = entry.startDateTime!.split('T');
    const newStart = `${datePart}T${time}:00`;
    this.scheduleService
      .updateEntryTime(this.tournamentId, this.selectedScheduleId, entry.id, newStart)
      .subscribe(() => this.selectSchedule(this.selectedScheduleId));
  }

  onChangeScheduleStartTime(time: string): void {
    const date = new Date(this.selectedSchedule.startDateTime);
    const [hours, minutes] = time.split(':').map(Number);
    date.setHours(hours);
    date.setMinutes(minutes);
    date.setSeconds(0);

    const local = date.toLocaleString('sv-SE').replace(' ', 'T');

    this.scheduleService
      .updateScheduleStartTime(this.tournamentId, this.selectedScheduleId, local)
      .subscribe(() => this.selectSchedule(this.selectedScheduleId));
  }

  recomputeTimesAndSave(): void {
  if (!this.selectedSchedule) return;

  const base = new Date(this.selectedSchedule.startDateTime);
  const newEntries: ScheduleEntry[] = [];

  this.scheduleEntries.forEach(entry => {
    entry.startDateTime = new Date(base).toISOString();
    newEntries.push({
      id: entry.id,
      type: entry.type,
      startDateTime: entry.startDateTime,
      durationInMin: entry.durationInMin,
      match: entry.match,
      schedule: this.selectedSchedule
    });
    base.setMinutes(base.getMinutes() + entry.durationInMin);
  });

  const ids = this.scheduleEntries.filter(e => e.id).map(e => e.id);

  this.scheduleService.reorderEntries(this.tournamentId, this.selectedScheduleId, ids)
    .subscribe(() => {
      this.scheduleService.recompute(this.tournamentId, this.selectedScheduleId)
        .subscribe(() => this.selectSchedule(this.selectedScheduleId));
    });
}

  removeEntry(entry: ScheduleEntry): void {
  this.scheduleService
    .removeEntryFromSchedule(this.tournamentId, this.selectedScheduleId, entry.id)
    .subscribe(() => this.selectSchedule(this.selectedScheduleId));
  }
}