import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Match } from '../models/match.model';
import { Observable } from 'rxjs';
import { BracketNode } from '../models/bracket-node.model';
import { Group } from '../models/group.model';

@Injectable({ providedIn: 'root' })
export class ResultService {
  constructor(private http: HttpClient) {}

  updateMatchResult(tournamentId: number, partialMatch: { id: number; homeScore: number; awayScore: number }) {
    return this.http.put(`/api/tournament/${tournamentId}/results/${partialMatch.id}`, {
      homeScore: partialMatch.homeScore,
      awayScore: partialMatch.awayScore
    });
  }

  getGroups(tournamentId: number): Observable<Group[]> {
    return this.http.get<Group[]>(`/api/tournament/${tournamentId}/results/groups`);
  }

  getBracket(tournamentId: number): Observable<BracketNode[]> {
    return this.http.get<BracketNode[]>(`/api/tournament/${tournamentId}/results/bracket`);
  }
}
