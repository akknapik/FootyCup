import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Schedule } from '../models/schedule.model';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class ScheduleService {
  constructor(private http: HttpClient) {}

  getSchedulesList(tournamentId: number): Observable<Schedule[]> {
    return this.http.get<Schedule[]>(`/api/tournament/${tournamentId}/schedule`);
  }

  getScheduleById(tournamentId: number, scheduleId: number): Observable<Schedule> {
    return this.http.get<Schedule>(`/api/tournament/${tournamentId}/schedule/${scheduleId}`);
  }

  addBreak(tournamentId: number, scheduleId: number, durationInMin: number): Observable<Schedule> {
    return this.http
      .post<void>(
        `/api/tournament/${tournamentId}/schedule/${scheduleId}/break`,
        null,
        { params: { duration: durationInMin.toString() } }
      )
      .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
  }

  recompute(tournamentId: number, scheduleId: number): Observable<Schedule> {
    return this.http
      .post<void>(`/api/tournament/${tournamentId}/schedule/${scheduleId}/recompute`, null)
      .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
  }

  reorderEntries(tournamentId: number, scheduleId: number, orderedEntryIds: number[]): Observable<Schedule> {
    return this.http
      .put<void>(`/api/tournament/${tournamentId}/schedule/${scheduleId}/order`, orderedEntryIds)
      .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
  }

  updateEntryTime(
    tournamentId: number,
    scheduleId: number,
    entryId: number,
    newStartIso: string
  ): Observable<Schedule> {
    return this.http
      .put<void>(
        `/api/tournament/${tournamentId}/schedule/${scheduleId}/${entryId}`,
        null,
        { params: { start: newStartIso } }
      )
      .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
  }

  addMatchToSchedule(
    tournamentId: number,
    scheduleId: number,
    matchId: number
  ): Observable<Schedule> {
    return this.http
      .post<void>(
        `/api/tournament/${tournamentId}/schedule/${scheduleId}/entry`,
        null,
        { params: { matchId: matchId.toString() } }
      )
      .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
  }

  removeEntryFromSchedule(
    tournamentId: number,
    scheduleId: number,
    entryId: number
  ): Observable<Schedule> {
    return this.http
      .delete<void>(`/api/tournament/${tournamentId}/schedule/${scheduleId}/entry/${entryId}`)
      .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
  }

  getUsedMatchIds(tournamentId: number): Observable<number[]> {
    return this.http.get<number[]>(`/api/tournament/${tournamentId}/schedule/used-match`);
  }

  updateScheduleStartTime(
  tournamentId: number,
  scheduleId: number,
  newStartIso: string
): Observable<Schedule> {
  return this.http
    .put<void>(
      `/api/tournament/${tournamentId}/schedule/${scheduleId}/start-time`,
      null,
      { params: { start: newStartIso } }
    )
    .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
}
}