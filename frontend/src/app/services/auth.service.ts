import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User|null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) { 
    const token = this.getToken();
    if (token) {
      this.loadCurrentUser().subscribe({
        error: () => {
          this.logout();
        }
      });
    }
  }

  register(data: any) {
    return this.http.post('/api/register', data, { responseType: 'text' });
  }
  
  login(data: {email: string, password: string}) {
    return this.http.post<{token: string }>('/api/login', data).pipe(
      tap((response) => {
        localStorage.setItem('token', response.token);
      }),
      tap(() => this.loadCurrentUser().subscribe())
    );
  }

  getToken() {
    return localStorage.getItem('token');
  }

  logout() {
    localStorage.removeItem('token');
  }

  loadCurrentUser() {
    return this.http.get<User>('/api/me').pipe(
      tap((user) => {
        this.currentUserSubject.next(user);
      })
    );
  }

  isLoggedIn(): boolean {
    const token = localStorage.getItem('token');
    return !!token;
  }
}
