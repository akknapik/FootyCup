import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MatchEventRef } from '../models/common/match-event-ref.model';
import { CreateMatchEventRequest } from '../models/match/create-match-event.request';

@Injectable({
  providedIn: 'root'
})
export class MatchEventService {

  constructor(private http: HttpClient) {}

  getEvents(tournamentId: number, matchId: number): Observable<MatchEventRef[]> {
    return this.http.get<MatchEventRef[]>(`/api/tournament/${tournamentId}/matches/${matchId}/events`, { withCredentials: true });
  }

  addEvent(tournamentId: number, matchId: number, payload: CreateMatchEventRequest): Observable<MatchEventRef> {
    return this.http.post<MatchEventRef>(`/api/tournament/${tournamentId}/matches/${matchId}/events`, payload, { withCredentials: true });
  }

  deleteEvent(tournamentId: number, matchId: number, eventId: number): Observable<void> {
    return this.http.delete<void>(`/api/tournament/${tournamentId}/matches/${matchId}/events/${eventId}`, { withCredentials: true });
  }
}