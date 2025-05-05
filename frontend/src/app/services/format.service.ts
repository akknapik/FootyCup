import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class FormatService {

  constructor(private http: HttpClient) { }

  structureExists(tournamentId: number) {
    return this.http.get<boolean>(`/api/tournament/${tournamentId}/format`);
  }

  generateGroupFormat(tournamentId: number, groupCount: number, teamsPerGroup: number) {
    return this.http.post<void>(`/api/tournament/${tournamentId}/format/group`, null, {
      params: { groupCount, teamsPerGroup }
    });
  }

  generateBracketFormat(tournamentId: number, totalTeams: number) {
    return this.http.post<void>(`/api/tournament/${tournamentId}/format/bracket`, null, {
      params: { totalTeams }
    });
  }

  generateMixedFormat(tournamentId: number, groupCount: number, teamsPerGroup: number, advancing: number) {
    return this.http.post<void>(`/api/tournament/${tournamentId}/format/mixed`, null, {
      params: { groupCount, teamsPerGroup, advancing }
    });
  }

  getGroups(tournamentId: number) {
    return this.http.get<any[]>(`/api/tournament/${tournamentId}/format/groups`);
  }

  getBracket(tournamentId: number) {
    return this.http.get<any[]>(`/api/tournament/${tournamentId}/format/bracket`);
  }
  
  deleteStructures(tournamentId: number) {
    return this.http.delete<void>(`/api/tournament/${tournamentId}/format`);
  }

  assignTeamsRandomly(tournamentId: number) {
    return this.http.post<void>(`/api/tournament/${tournamentId}/format/assign-random`, null);
  }

  assignTeamToSlot(tournamentId: number, slotId: number,  teamId: number) {
    return this.http.put<void>(`/api/tournament/${tournamentId}/format/${slotId}/assign/${teamId}`, null); 
  }

  assignTeamToBracketNode(tournamentId: number, nodeId: number, teamId: number, homeTeam: boolean) {
    return this.http.put<void>(`/api/tournament/${tournamentId}/format/bracket/${nodeId}`, null, {
      params: {
        teamId: teamId.toString(),
        homeTeam: homeTeam.toString()
      }
    });
  }
}
