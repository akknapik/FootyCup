import { Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { forkJoin } from 'rxjs';
import { ScheduleService } from '../../services/schedule.service';
import { MatchService } from '../../services/match.service';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { MatchItemResponse } from '../../models/match/match-item.response';
import { ScheduleResponse } from '../../models/schedule/schedule.response';
import { ScheduleEntryResponse } from '../../models/schedule/schedule-entry.response';
import { MatchRef } from '../../models/common/match-ref.model';

@Component({
  selector: 'app-schedule',
  standalone: false,
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css']
})
export class ScheduleComponent implements OnInit {
  tournamentId!: number;
  schedules: { id: number; startDateTime: Date }[] = [];
  selectedSchedule!: ScheduleResponse;
  selectedScheduleId!: number;
  allMatches: MatchItemResponse[] = [];
  scheduleEntries: ScheduleEntryResponse[] = [];
  breakDuration: number = 15;
  isLoading: boolean = false;
  loadingMatches: boolean = false;

  constructor(
    private route: ActivatedRoute,
    public router: Router,
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
  this.isLoading = true;
  this.scheduleService.getSchedulesList(this.tournamentId).subscribe({
    next: (list) => {
      this.schedules = list
        .map(s => ({
          id: s.id!,
          startDateTime: new Date(s.startDateTime!)
        }))
        .sort((a, b) => a.startDateTime.getTime() - b.startDateTime.getTime());

      if (this.schedules.length) {
        this.selectSchedule(this.schedules[0].id);
      } else {
        this.isLoading = false;
      }
    },
    error: () => {
      this.notification.showError('Failed to load schedules');
      this.isLoading = false;
    }
  });
}

  selectSchedule(id: number): void {
  this.loadingMatches = true;
  this.selectedScheduleId = id;

  forkJoin({
    sched: this.scheduleService.getScheduleById(this.tournamentId, id),
    matches: this.matchService.getMatches(this.tournamentId),
    usedIds: this.scheduleService.getUsedMatchIds(this.tournamentId)
  }).subscribe({
    next: ({ sched, matches, usedIds }) => {
      this.scheduleEntries = sched.entries;
      this.selectedSchedule = sched;
      const usedIdSet = new Set(usedIds);
      this.allMatches = matches.filter(m => !usedIdSet.has(m.id));
      this.loadingMatches = false;
      this.isLoading = false;
    },
    error: () => {
      this.notification.showError('Error loading schedule details');
      this.loadingMatches = false;
      this.isLoading = false;
    }
  });
}


onAddBreak(durationInMin: number): void {
  this.scheduleService
    .addBreak(this.tournamentId, this.selectedScheduleId, { durationInMin })
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
      .addMatchToSchedule(this.tournamentId, this.selectedScheduleId, { matchId: match.id })
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
              .reorderEntries(this.tournamentId, this.selectedScheduleId, { orderedEntryIds:ids })
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
      this.allMatches.push(this.mapMatchRefToItem(removed.match));
    }

    this.scheduleService
      .removeEntryFromSchedule(this.tournamentId, this.selectedScheduleId, removed.id)
      .subscribe(() => this.selectSchedule(this.selectedScheduleId));
  }
}

  updateTime(entry: ScheduleEntryResponse, time: string): void {
    const [datePart] = entry.startDateTime!.split('T');
    const newStart = `${datePart}T${time}:00`;
    this.scheduleService
      .updateEntryTime(this.tournamentId, this.selectedScheduleId, entry.id, { start: newStart })
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
      .updateScheduleStartTime(this.tournamentId, this.selectedScheduleId, {start: local})
      .subscribe(() => this.selectSchedule(this.selectedScheduleId));
  }

  recomputeTimesAndSave(): void {
    if (!this.selectedSchedule) return;

    const ids = this.scheduleEntries.map(e => e.id);

    this.scheduleService
      .reorderEntries(this.tournamentId, this.selectedScheduleId, { orderedEntryIds: ids })
      .subscribe(() => {
        this.scheduleService
          .recompute(this.tournamentId, this.selectedScheduleId)
          .subscribe(() => this.selectSchedule(this.selectedScheduleId));
      });
  }

  removeEntry(entry: ScheduleEntryResponse): void {
    this.scheduleService
      .removeEntryFromSchedule(this.tournamentId, this.selectedScheduleId, entry.id)
      .subscribe(() => this.selectSchedule(this.selectedScheduleId));
  }

    logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }

  private mapMatchRefToItem(m: MatchRef): MatchItemResponse {
  return {
    id: m.id,
    name: m.name,
    status: 'NOT_SCHEDULED',
    teamHome: { id: m.teamHomeId, name: m.teamHomeName ?? '—' },
    teamAway: { id: m.teamAwayId, name: m.teamAwayName ?? '—' },
    referee: null
  };
}

openedMenuEntryId: number | null = null;

toggleEntryMenu(e: ScheduleEntryResponse) {
  this.openedMenuEntryId = this.openedMenuEntryId === e.id ? null : e.id;
}
isEntryMenuOpen(e: ScheduleEntryResponse) {
  return this.openedMenuEntryId === e.id;
}

updateTimeUI(entry: ScheduleEntryResponse, time: string) {
  const [datePart] = entry.startDateTime.split('T');
  const newStart = `${datePart}T${time}:00`;
  this.scheduleService
    .updateEntryTime(this.tournamentId, this.selectedScheduleId, entry.id, { start: newStart })
    .subscribe(() => this.selectSchedule(this.selectedScheduleId));
}

@HostListener('document:click', ['$event'])
closeMenusOutside(ev: MouseEvent) {
  const el = ev.target as HTMLElement;
  if (!el.closest('.row-actions')) this.openedMenuEntryId = null;
}

}
