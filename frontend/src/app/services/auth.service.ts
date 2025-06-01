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
  this.loadCurrentUser().subscribe({
    error: () => {
      this.currentUserSubject.next(null);
    }
  });
}

  register(data: any) {
    return this.http.post('/api/register', data, { responseType: 'text' });
  }
  
  login(data: { email: string, password: string }) {
  return this.http.post('/api/login', data, { withCredentials: true }).pipe(
    tap(() => this.loadCurrentUser().subscribe())
  );
}

  getToken() {
    return localStorage.getItem('token');
  }

  logout() {
  return this.http.post('/api/logout', {}, { withCredentials: true }).pipe(
    tap(() => {
      this.currentUserSubject.next(null);
    })
  );
}

  loadCurrentUser() {
    return this.http.get<User>('/api/me').pipe(
      tap((user) => {
        this.currentUserSubject.next(user);
      })
    );
  }

  isLoggedIn(): boolean {
    return !!this.currentUserSubject.value;
  }
}
