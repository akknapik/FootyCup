import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ScheduleListItemResponse } from '../models/schedule/schedule-list-item.response';
import { ScheduleResponse } from '../models/schedule/schedule.response';
import { AddBreakRequest } from '../models/schedule/add-break.request';
import { ReorderEntriesRequest } from '../models/schedule/reorder-entries.request';
import { UpdateEntryTimeRequest } from '../models/schedule/update-entry-time.request';
import { AddMatchEntryRequest } from '../models/schedule/add-match-entry.request';
import { UpdateScheduleStartTimeRequest } from '../models/schedule/update-schedule-start-time.request';

@Injectable({ providedIn: 'root' })
export class ScheduleService {
  constructor(private http: HttpClient) {}

  getSchedulesList(tournamentId: number): Observable<ScheduleListItemResponse[]> {
    return this.http.get<ScheduleListItemResponse[]>(`/api/tournament/${tournamentId}/schedule`, { withCredentials: true });
  }

  getScheduleById(tournamentId: number, scheduleId: number): Observable<ScheduleResponse> {
    return this.http.get<ScheduleResponse>(`/api/tournament/${tournamentId}/schedule/${scheduleId}`, { withCredentials: true });
  }

  addBreak(tournamentId: number, scheduleId: number, data: AddBreakRequest): Observable<ScheduleResponse> {
    return this.http
      .post<void>(
        `/api/tournament/${tournamentId}/schedule/${scheduleId}/break`,
        data,
        { withCredentials: true }
      )
      .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
  }

  recompute(tournamentId: number, scheduleId: number): Observable<ScheduleResponse> {
    return this.http
      .post<void>(`/api/tournament/${tournamentId}/schedule/${scheduleId}/recompute`, null, { withCredentials: true })
      .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
  }

  reorderEntries(tournamentId: number, scheduleId: number, data: ReorderEntriesRequest): Observable<ScheduleResponse> {
    return this.http
      .put<void>(`/api/tournament/${tournamentId}/schedule/${scheduleId}/order`, data, { withCredentials: true })
      .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
  }

  updateEntryTime(
    tournamentId: number,
    scheduleId: number,
    entryId: number,
    data: UpdateEntryTimeRequest
  ): Observable<ScheduleResponse> {
    return this.http
      .put<void>(
        `/api/tournament/${tournamentId}/schedule/${scheduleId}/${entryId}`,
        data,
        { withCredentials: true  }
      )
      .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
  }

  addMatchToSchedule(
    tournamentId: number,
    scheduleId: number,
    data: AddMatchEntryRequest
  ): Observable<ScheduleResponse> {
    return this.http
      .post<void>(
        `/api/tournament/${tournamentId}/schedule/${scheduleId}/entry`,
        data,
        { params: { withCredentials: true } }
      )
      .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
  }

  removeEntryFromSchedule(
    tournamentId: number,
    scheduleId: number,
    entryId: number
  ): Observable<ScheduleResponse> {
    return this.http
      .delete<void>(`/api/tournament/${tournamentId}/schedule/${scheduleId}/entry/${entryId}`, { withCredentials: true })
      .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
  }

  getUsedMatchIds(tournamentId: number): Observable<number[]> {
    return this.http.get<number[]>(`/api/tournament/${tournamentId}/schedule/used-match`, { withCredentials: true });
  }

  updateScheduleStartTime(
  tournamentId: number,
  scheduleId: number,
  data: UpdateScheduleStartTimeRequest
): Observable<ScheduleResponse> {
  return this.http
    .put<void>(
      `/api/tournament/${tournamentId}/schedule/${scheduleId}/start-time`,
      data,
      { withCredentials: true  }
    )
    .pipe(switchMap(() => this.getScheduleById(tournamentId, scheduleId)));
}
}
