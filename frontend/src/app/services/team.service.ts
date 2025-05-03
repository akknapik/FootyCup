import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Team } from '../models/team.model';

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  constructor(private http: HttpClient) {}

  getTeams(tournamentId: number) {
    return this.http.get<Team[]>(`/api/tournament/${tournamentId}/teams`);
  }

  getTeamById(tournamentId: number, teamId: number) {
    return this.http.get<Team>(`/api/tournament/${tournamentId}/teams/${teamId}`);
  }

  createTeam(tournamentId: number, data: any) {
    return this.http.post(`/api/tournament/${tournamentId}/teams`, data);
  }

  updateTeam(tournamentId: number, teamId: number, data: any) {
    return this.http.put(`/api/tournament/${tournamentId}/teams/${teamId}`, data);
  }

  deleteTeam(tournamentId: number, teamId: number) {
    return this.http.delete(`/api/tournament/${tournamentId}/teams/${teamId}`);
  }

  addPlayerToTeam(tournamentId: number, teamId: number, data: any) {
    return this.http.post(`/api/tournament/${tournamentId}/teams/${teamId}/players`, data);
  }

  updatePlayerInTeam(tournamentId: number, teamId: number, playerId: number, data: any) {
    return this.http.put(`/api/tournament/${tournamentId}/teams/${teamId}/players/${playerId}`, data);
  }

  removePlayerFromTeam(tournamentId: number, teamId: number, playerId: number) {
    return this.http.delete(`/api/tournament/${tournamentId}/teams/${teamId}/players/${playerId}`);
  }
}
