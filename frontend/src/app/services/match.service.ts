import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MatchItemResponse } from '../models/match/match-item.response';
import { MatchResponse } from '../models/match/match.response';

@Injectable({
  providedIn: 'root'
})
export class MatchService {

  constructor(private http: HttpClient) { }

  getMatches(tournamentId: number): Observable<MatchItemResponse[]> {
    return this.http.get<MatchItemResponse[]>(`/api/tournament/${tournamentId}/matches`, { withCredentials: true });
  }

  getMatch(tournamentId: number, matchId: number) {
    return this.http.get<MatchResponse>(`/api/tournament/${tournamentId}/matches/${matchId}`, { withCredentials: true });
  }

  generateGroupMatches(tournamentId: number): Observable<void>{
    return this.http.post<void>(
      `/api/tournament/${tournamentId}/matches`,
      null, { withCredentials: true }
    );
  }

  deleteMatch(tournamentId: number, matchId: number): Observable<void> {
    return this.http.delete<void>(`/api/tournament/${tournamentId}/matches/${matchId}`, { withCredentials: true });
  }

  deleteAllMatches(tournamentId: number): Observable<void> {
    return this.http.delete<void>(`/api/tournament/${tournamentId}/matches`, { withCredentials: true });
  }

  assignReferee(tournamentId: number, matchId: number, refereeId: number): Observable<MatchItemResponse> {
    return this.http.put<MatchItemResponse>(`/api/tournament/${tournamentId}/matches/${matchId}/referee`, null, {
      params: new HttpParams().set('refereeId', refereeId),
      withCredentials: true,
    });
  }
}
