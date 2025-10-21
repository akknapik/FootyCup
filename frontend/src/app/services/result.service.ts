import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BracketNode } from '../models/bracket-node.model';
import { Group } from '../models/group.model';
import { UpdateMatchResultRequest } from '../models/result/update-match-result.request';
import { GroupResponse } from '../models/format/group/group.response';
import { BracketNodeResponse } from '../models/format/bracket/bracket-node.response';

@Injectable({ providedIn: 'root' })
export class ResultService {
  constructor(private http: HttpClient) {}

  updateMatchResult(tournamentId: number, data: UpdateMatchResultRequest) {
    return this.http.put(`/api/tournament/${tournamentId}/results/${data.matchId}`, data, { withCredentials: true });
  }

  getGroups(tournamentId: number): Observable<GroupResponse[]> {
    return this.http.get<GroupResponse[]>(`/api/tournament/${tournamentId}/results/groups`, { withCredentials: true });
  }

  getBracket(tournamentId: number): Observable<BracketNodeResponse[]> {
    return this.http.get<BracketNodeResponse[]>(`/api/tournament/${tournamentId}/results/bracket`, { withCredentials: true });
  }
}
