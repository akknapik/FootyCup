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
}