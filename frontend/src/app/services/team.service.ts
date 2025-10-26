import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Team } from '../models/team.model';
import { TeamItemResponse } from '../models/team/team-item.response';
import { TeamResponse } from '../models/team/team.response';
import { CreateTeamRequest } from '../models/team/create-team.request';
import { UpdateTeamRequest } from '../models/team/update-team.request';
import { CreatePlayerRequest } from '../models/team/create-player.request';
import { UpdatePlayerRequest } from '../models/team/update-player.request';
import { PlayerStatisticsResponse } from '../models/team/player-statistics.response';
import { TeamStatisticsResponse } from '../models/team/team-statistics.response';

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  constructor(private http: HttpClient) {}

  getTeams(tournamentId: number) {
    return this.http.get<TeamItemResponse[]>(`/api/tournament/${tournamentId}/teams`, { withCredentials: true });
  }

  getTeamById(tournamentId: number, teamId: number) {
    return this.http.get<TeamResponse>(`/api/tournament/${tournamentId}/teams/${teamId}`, { withCredentials: true });
  }

  createTeam(tournamentId: number, data: CreateTeamRequest) {
    return this.http.post<TeamResponse>(`/api/tournament/${tournamentId}/teams`, data, { withCredentials: true });
  }

  updateTeam(tournamentId: number, teamId: number, data: UpdateTeamRequest) {
    return this.http.put<TeamResponse>(`/api/tournament/${tournamentId}/teams/${teamId}`, data, { withCredentials: true });
  }

  deleteTeam(tournamentId: number, teamId: number) {
    return this.http.delete<void>(`/api/tournament/${tournamentId}/teams/${teamId}`, { withCredentials: true });
  }

  addPlayerToTeam(tournamentId: number, teamId: number, data: CreatePlayerRequest) {
    return this.http.post<TeamResponse>(`/api/tournament/${tournamentId}/teams/${teamId}/players`, data, { withCredentials: true });
  }

  updatePlayerInTeam(tournamentId: number, teamId: number, playerId: number, data: UpdatePlayerRequest) {
    return this.http.put<TeamResponse>(`/api/tournament/${tournamentId}/teams/${teamId}/players/${playerId}`, data, { withCredentials: true });
  }

  removePlayerFromTeam(tournamentId: number, teamId: number, playerId: number) {
    return this.http.delete<TeamResponse>(`/api/tournament/${tournamentId}/teams/${teamId}/players/${playerId}`, { withCredentials: true });
  }

  getPlayerStatistics(tournamentId: number, teamId: number, playerId: number) {
    return this.http.get<PlayerStatisticsResponse>(
      `/api/tournament/${tournamentId}/teams/${teamId}/players/${playerId}/statistics`,
      { withCredentials: true }
    );
  }

  getTeamStatistics(tournamentId: number, teamId: number) {
    return this.http.get<TeamStatisticsResponse>(
      `/api/tournament/${tournamentId}/teams/${teamId}/statistics`,
      { withCredentials: true }
    );
  }
}
