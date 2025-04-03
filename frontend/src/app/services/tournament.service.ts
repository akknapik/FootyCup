import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Tournament } from "../models/tournament.model";

@Injectable({ providedIn: 'root' })
export class TournamentService {
  constructor(private http: HttpClient) {}

  getMyTournaments() {
    return this.http.get<Tournament[]>('/api/tournaments/my');
  }

  createTournament(data: any) {
    return this.http.post('/api/tournaments', data);
  }

  getTournamentById(id: number) {
    return this.http.get<any>(`/api/tournaments/${id}`);
  }
  
  updateTournament(id: number, data: any) {
    return this.http.put(`/api/tournaments/${id}`, data);
  }

  deleteTournament(id: number) {
    return this.http.delete(`/api/tournaments/${id}`);
  }
}