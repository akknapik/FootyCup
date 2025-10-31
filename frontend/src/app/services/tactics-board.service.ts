import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { TacticsBoardState } from '../models/tactics/tactics-board-state.request';
import { TacticsBoardResponse } from '../models/tactics/tactics-board.response';

@Injectable({
  providedIn: 'root'
})
export class TacticsBoardService {

  constructor(private http: HttpClient) {}

  load(
    tournamentId: number,
    matchId: number,
    teamId?: number | null
  ): Observable<TacticsBoardState | null> {
    return this.http
      .get<TacticsBoardResponse>(
        `/api/tournament/${tournamentId}/matches/${matchId}/tactics-board`,
        this.buildOptions(teamId)
      )
      .pipe(
        map(response => this.toState(response)),
        catchError(error => {
          if (error.status === 404) {
            return of(null);
          }
          return throwError(() => error);
        })
      );
  }

  save(
    tournamentId: number,
    matchId: number,
    state: TacticsBoardState,
    teamId?: number | null
  ): Observable<TacticsBoardState> {
    return this.http
      .put<TacticsBoardResponse>(
        `/api/tournament/${tournamentId}/matches/${matchId}/tactics-board`,
        state,
        this.buildOptions(teamId)
      )
      .pipe(map(response => this.toState(response)));
  }

  clear(tournamentId: number, matchId: number, teamId?: number | null): Observable<void> {
    return this.http.delete<void>(
      `/api/tournament/${tournamentId}/matches/${matchId}/tactics-board`,
      this.buildOptions(teamId)
    );
  }

  private toState(response: TacticsBoardResponse): TacticsBoardState {
    return {
      layers: response.layers ?? [],
      activeLayerId: response.activeLayerId ?? null,
      lastUpdated: response.lastUpdated ?? new Date().toISOString()
    };
  }

  private buildOptions(teamId?: number | null) {
    if (teamId === undefined || teamId === null) {
      return { withCredentials: true };
    }
    const params = new HttpParams().set('teamId', String(teamId));
    return { withCredentials: true, params };
  }
}