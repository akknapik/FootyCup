import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { GenerateGroupRequest } from '../models/format/group/generate-group.request';
import { GenerateBracketRequest } from '../models/format/bracket/generate-bracket.request';
import { GenerateMixRequest } from '../models/format/generate-mix.request';
import { GroupItemResponse } from '../models/format/group/group-item.response';
import { BracketNodeResponse } from '../models/format/bracket/bracket-node.response';
import { AssignTeamToSlotRequest } from '../models/format/group/assign-team-to-slot.request';
import { AssignTeamToNodeRequest } from '../models/format/bracket/assign-team-to-node.request';
import { GroupResponse } from '../models/format/group/group.response';

@Injectable({
  providedIn: 'root'
})
export class FormatService {

  constructor(private http: HttpClient) { }

  structureExists(tournamentId: number) {
    return this.http.get<boolean>(`/api/tournament/${tournamentId}/format`, { withCredentials: true });
  }

  generateGroupFormat(tournamentId: number, payload: GenerateGroupRequest) {
    return this.http.post<void>(`/api/tournament/${tournamentId}/format/group`, payload, {
      withCredentials: true
    });
  }

  generateBracketFormat(tournamentId: number, payload: GenerateBracketRequest) {
    return this.http.post<void>(`/api/tournament/${tournamentId}/format/bracket`, payload, {
      withCredentials: true
    });
  }

  generateMixedFormat(tournamentId: number, payload: GenerateMixRequest) {
    return this.http.post<void>(`/api/tournament/${tournamentId}/format/mixed`, payload, {
      withCredentials: true
    });
  }

  getGroups(tournamentId: number) {
    return this.http.get<GroupResponse[]>(`/api/tournament/${tournamentId}/format/groups`, { withCredentials: true });
  }

  getBracket(tournamentId: number) {
    return this.http.get<BracketNodeResponse[]>(`/api/tournament/${tournamentId}/format/bracket`, { withCredentials: true });
  }

  deleteStructures(tournamentId: number) {
    return this.http.delete<void>(`/api/tournament/${tournamentId}/format`, { withCredentials: true });
  }

  assignTeamsRandomly(tournamentId: number) {
    return this.http.post<void>(`/api/tournament/${tournamentId}/format/assign-random`, null, { withCredentials: true });
  }

  assignTeamToSlot(tournamentId: number, payload: AssignTeamToSlotRequest) {
    return this.http.put<void>(`/api/tournament/${tournamentId}/format/slot`, payload, {
      withCredentials: true
    });
  }

  assignTeamToBracketNode(tournamentId: number, payload: AssignTeamToNodeRequest) {
    return this.http.put<void>(`/api/tournament/${tournamentId}/format/bracket`, payload, {
      withCredentials: true
    });
  }
}
