import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class FormatService {

  constructor(private http: HttpClient) { }

  structureExists(tournamentId: number) {
    return this.http.get<boolean>(`/api/tournament/${tournamentId}/format`, { withCredentials: true });
  }

  generateGroupFormat(tournamentId: number, groupCount: number, teamsPerGroup: number) {
    return this.http.post<void>(`/api/tournament/${tournamentId}/format/group`, null, {
      params: { groupCount, teamsPerGroup },
      withCredentials: true
    });
  }

  generateBracketFormat(tournamentId: number, totalTeams: number) {
    return this.http.post<void>(`/api/tournament/${tournamentId}/format/bracket`, null, {
      params: { totalTeams },
      withCredentials: true
    });
  }

  generateMixedFormat(tournamentId: number, groupCount: number, teamsPerGroup: number, advancing: number) {
    return this.http.post<void>(`/api/tournament/${tournamentId}/format/mixed`, null, {
      params: { groupCount, teamsPerGroup, advancing },
      withCredentials: true
    });
  }

  getGroups(tournamentId: number) {
    return this.http.get<any[]>(`/api/tournament/${tournamentId}/format/groups`, { withCredentials: true });
  }

  getBracket(tournamentId: number) {
    return this.http.get<any[]>(`/api/tournament/${tournamentId}/format/bracket`, { withCredentials: true });
  }

  deleteStructures(tournamentId: number) {
    return this.http.delete<void>(`/api/tournament/${tournamentId}/format`, { withCredentials: true });
  }

  assignTeamsRandomly(tournamentId: number) {
    return this.http.post<void>(`/api/tournament/${tournamentId}/format/assign-random`, null, { withCredentials: true });
  }

  assignTeamToSlot(tournamentId: number, slotId: number, teamId: number) {
    return this.http.put<void>(`/api/tournament/${tournamentId}/format/${slotId}/assign/${teamId}`, null, {
      withCredentials: true
    });
  }

  assignTeamToBracketNode(tournamentId: number, nodeId: number, teamId: number, homeTeam: boolean) {
    return this.http.put<void>(`/api/tournament/${tournamentId}/format/bracket/${nodeId}`, null, {
      params: {
        teamId: teamId.toString(),
        homeTeam: homeTeam.toString()
      },
      withCredentials: true
    });
  }
}
