import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Match } from '../models/match.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MatchService {

  constructor(private http: HttpClient) { }

  getMatches(tournamentId: number): Observable<Match[]> {
    return this.http.get<Match[]>(`/api/tournament/${tournamentId}/matches`);
  }

  getMatch(tournamentId: number, matchId: number) {
    return this.http.get<Match>(`/api/tournament/${tournamentId}/matches/${matchId}`);
  }

  generateGroupMatches(tournamentId: number, rematch: boolean): Observable<void>{
    return this.http.post<void>(
      `/api/tournament/${tournamentId}/matches?rematch=${rematch}`,
      null
    );
  }

  deleteMatch(tournamentId: number, matchId: number): Observable<void> {
    return this.http.delete<void>(`/api/tournament/${tournamentId}/matches/${matchId}`);
  }

  deleteAllMatches(tournamentId: number): Observable<void> {
    return this.http.delete<void>(`/api/tournament/${tournamentId}/matches`);
  }
}
