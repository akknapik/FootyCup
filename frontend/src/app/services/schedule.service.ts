import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Schedule } from '../models/schedule.model';
import { Observable } from 'rxjs';
import { ScheduleEntry } from '../models/schedule-entry.model';

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {

  constructor(private http: HttpClient) { }

  getSchedule(tournamentId: number) {
    return this.http.get<Schedule>(`/api/tournament/${tournamentId}/schedule`);
  }

  createSchedule(tournamentId: number, startIso: string): Observable<void> {
    return this.http.post<void>(`/api/tournament/${tournamentId}/schedule`, null, {
      params: { start: startIso }
    });
  }

  addBreak(tournamentId: number, scheduleId: number, durationInMin: number): Observable<void> {
    return this.http.post<void>(`/api/tournament/${tournamentId}/schedule/${scheduleId}/break`, null, {
      params: { duration: durationInMin.toString() }
    });
  }

  recompute(tournamentId: number, scheduleId: number): Observable<void> {
    return this.http.post<void>(`/api/tournament/${tournamentId}/schedule/${scheduleId}/recompute`, null);
  }

  reorderEntries(tournamentId: number, scheduleId: number, orderedEntryIds: number[]): Observable<void> {
    return this.http.put<void>(`/api/tournament/${tournamentId}/schedule/${scheduleId}/order`, orderedEntryIds);
  }

  updateEntryTime(tournamentId: number, scheduleId: number, entryId: number, newStartIso : string): Observable<void> {
    return this.http.put<void>(`/api/tournament/${tournamentId}/schedule/${scheduleId}/${entryId}`, null, {
      params: { start: newStartIso }
    });
  }

  addMatchToSchedule(tournamentId: number, scheduleId: number, matchId: number) {
    return this.http.post<void>(
      `/api/tournament/${tournamentId}/schedule/${scheduleId}/entry`,
      null,
      { params: { matchId: matchId.toString() } }
    );
  }

  removeEntryFromSchedule(tournamentId: number, scheduleId: number, entryId: number): Observable<void> {
    return this.http.delete<void>(
      `/api/tournament/${tournamentId}/schedule/${scheduleId}/entry/${entryId}`
    );
  }
}

