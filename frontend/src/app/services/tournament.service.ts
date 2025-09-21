import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Tournament } from "../models/tournament.model";
import { BehaviorSubject, filter } from "rxjs";
import { NavigationEnd, Router } from "@angular/router";
import { User } from "../models/user.model";

@Injectable({ providedIn: 'root' })
export class TournamentService {
  private tournamentIdSubject = new BehaviorSubject<string | null>(null);
  tournamentId$ = this.tournamentIdSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.router.events
    .pipe(filter(e => e instanceof NavigationEnd))
    .subscribe(() => {
      const match = this.router.url.match(/\/tournaments\/(\d+)/);
      this.tournamentIdSubject.next(match ? match[1] : null);
    });
  }

  getMyTournaments() {
    return this.http.get<Tournament[]>('/api/tournaments/my', { withCredentials: true });
  }

  createTournament(data: any) {
    return this.http.post('/api/tournaments', data, { withCredentials: true });
  }

  getTournamentById(id: number) {
    return this.http.get<Tournament>(`/api/tournaments/${id}`, { withCredentials: true });
  }
  
  updateTournament(id: number, data: any) {
    return this.http.put(`/api/tournaments/${id}`, data, { withCredentials: true });
  }

  deleteTournament(id: number) {
    return this.http.delete(`/api/tournaments/${id}`, { withCredentials: true });
  }

  getReferees(tournamentId: number) {
    return this.http.get<User[]>(`/api/tournaments/${tournamentId}/referees`, { withCredentials: true });
  }

  addReferee(id: number, email: string) {
    return this.http.post<User[]>(`/api/tournaments/${id}/referees`, null, {
      params: new HttpParams().set('email', email),
      withCredentials: true,
    });
  }

  removeReferee(tournamentId: number, userId: number) {
    return this.http.delete<Tournament>(`/api/tournaments/${tournamentId}/referees/${userId}`, { withCredentials: true });
  }
  
  get currentId(): string | null {
    return this.tournamentIdSubject.value;
  }
}