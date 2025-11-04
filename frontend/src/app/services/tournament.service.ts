import { HttpClient, HttpParams, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Tournament } from "../models/tournament.model";
import { BehaviorSubject, filter, Observable } from "rxjs";
import { NavigationEnd, Router } from "@angular/router";
import { User } from "../models/user.model";
import { TournamentItemResponse } from "../models/tournament/tournament-item.response";
import { CreateTournamentRequest } from "../models/tournament/create-tournament.request";
import { TournamentResponse } from "../models/tournament/tournament.response";
import { UpdateTournamentRequest } from "../models/tournament/update-tournament.request";
import { UserRef } from "../models/common/user-ref.model";
import { AddRefereeRequest } from "../models/tournament/add-referee.request";
import { MyTournamentsResponse } from "../models/tournament/my-tournaments.response";
import { TournamentQrCodeResponse } from "../models/tournament/tournament-qr-code.response";

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
    return this.http.get<MyTournamentsResponse>('/api/tournaments/my', { withCredentials: true });
  }

  getPublicTournaments() {
    return this.http.get<TournamentItemResponse[]>('/api/tournaments/public');
  }

  getPublicTournamentById(id: number) {
    return this.http.get<TournamentResponse>(`/api/tournaments/public/${id}`);
  }

  createTournament(data: CreateTournamentRequest) {
    return this.http.post<TournamentResponse>('/api/tournaments', data, { withCredentials: true });
  }

  getTournamentById(id: number) {
    return this.http.get<TournamentResponse>(`/api/tournaments/${id}`, { withCredentials: true });
  }
  
  updateTournament(id: number, data: UpdateTournamentRequest) {
    return this.http.put<TournamentResponse>(`/api/tournaments/${id}`, data, { withCredentials: true });
  }

  deleteTournament(id: number) {
    return this.http.delete<void>(`/api/tournaments/${id}`, { withCredentials: true });
  }

  getReferees(tournamentId: number) {
    return this.http.get<UserRef[]>(`/api/tournaments/${tournamentId}/referees`, { withCredentials: true });
  }

  addReferee(id: number, email: string) {
    const body: AddRefereeRequest = { email };
    return this.http.post<UserRef[]>(`/api/tournaments/${id}/referees`, body, { withCredentials: true });
  }

  removeReferee(tournamentId: number, userId: number) {
    return this.http.delete<void>(`/api/tournaments/${tournamentId}/referees/${userId}`, { withCredentials: true });
  }

  followTournament(id: number) {
    return this.http.post<void>(`/api/tournaments/${id}/follow`, {}, { withCredentials: true });
  }

  unfollowTournament(id: number) {
    return this.http.delete<void>(`/api/tournaments/${id}/follow`, { withCredentials: true });
  }

  generateTournamentQrCode(id: number) {
    return this.http.post<TournamentQrCodeResponse>(`/api/tournaments/${id}/qr-code`, {}, { withCredentials: true });
  }

  getTournamentQrCode(id: number) {
    return this.http.get<TournamentQrCodeResponse>(`/api/tournaments/${id}/qr-code`, { withCredentials: true });
  }

  downloadTournamentQrCode(id: number) {
    return this.http.get<Blob>(`/api/tournaments/${id}/qr-code/download`, {
      responseType: 'blob' as 'json',
      withCredentials: true
    });
  }
  
  exportTournament(id: number, format: 'pdf' | 'csv'): Observable<HttpResponse<Blob>> {
    return this.http.get(`/api/tournaments/${id}/export`, {
      params: new HttpParams().set('format', format),
      responseType: 'blob',
      observe: 'response',
      withCredentials: true
    });
  }

  get currentId(): string | null {
    return this.tournamentIdSubject.value;
  }
}