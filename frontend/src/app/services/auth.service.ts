import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, of, tap, throwError } from 'rxjs';
import { User } from '../models/user.model';
import { NotificationService } from './notification.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User|null>(null);
  currentUser$ = this.currentUserSubject.asObservable();
  private tokenTimeout: any;

  constructor(private http: HttpClient, private notification: NotificationService, private router: Router
  ) {
    this.loadCurrentUser().subscribe();
  }


  register(data: any) {
    return this.http.post('/api/register', data, { responseType: 'text',
    withCredentials: true });
  }
  
login(data: { email: string, password: string }) {
  return this.http.post<{ expiresIn: number }>('/api/login', data, { withCredentials: true }).pipe(
    tap((res) => {
      this.startTokenWatcher(res.expiresIn);

      this.loadCurrentUser().subscribe({
        next: () => {},
        error: (err) => {
          this.notification.showError('Error loading user data after login.');
        }
      });
    })
  );
}

refreshToken() {
  return this.http.post<{ expiresIn: number }>('/api/refresh', {}, { withCredentials: true }).pipe(
    tap((res) => {
      this.startTokenWatcher(res.expiresIn);
    })
  );
}

startTokenWatcher(expiresInSeconds: number) {
  clearTimeout(this.tokenTimeout);

  const warningTime = (expiresInSeconds - 30) * 1000;

  if (warningTime <= 0) return;

  this.tokenTimeout = setTimeout(() => {

    this.notification.confirm('Session will expire soon. Extend?').subscribe((confirmed) => {
      if (confirmed) {
        this.refreshToken().subscribe({
          next: (res: any) => {
            this.startTokenWatcher(res.expiresIn);
          },
          error: () => {
            this.logout().subscribe();
            this.notification.showError('Your session has expired, please log in again.');
          }
        });
      } else {
        this.logout().subscribe();
        this.notification.showSuccess('You have been logged out due to inactivity.');
      }
    });
  }, warningTime);
  }

  requestPasswordReset(email: string) {
    return this.http.post('/api/forgot-password', { email }, { responseType: 'text' });
  }

  resetPassword(token: string, password: string) {
    return this.http.post('/api/reset-password', { token, password }, { responseType: 'text' });
  }

  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }

  logout() {
  if (this.tokenTimeout) {
    clearTimeout(this.tokenTimeout);
  }

  return this.http.post('/api/logout', {}, { withCredentials: true }).pipe(
  tap(() => {
    this.currentUserSubject.next(null);
  })
);
}

 loadCurrentUser(): Observable<User | null> {
  return this.http.get<User>('/api/users/me', {
    withCredentials: true
  }).pipe(
    tap(user => this.currentUserSubject.next(user)),
    catchError(err => {
      if (err.status === 401) {
        this.currentUserSubject.next(null);
        return of(null); 
      }
      return throwError(() => err);
    })
  );
}

  isLoggedIn(): boolean {
    return !!this.currentUserSubject.value;
  }

  get currentUserRole(): string | null {
  const token = this.getAccessTokenFromCookie();
  if (!token) return null;

  try {
    const decoded = JSON.parse(atob(token.split('.')[1]));
    return decoded?.role || null;
  } catch (e) {
    return null;
  }
}

  public getAccessTokenFromCookie(): string | null {
    const name = 'accessToken=';
    const decodedCookie = decodeURIComponent(document.cookie);
    const ca = decodedCookie.split(';');
    for (let i = 0; i < ca.length; i++) {
      let c = ca[i].trim();
      if (c.indexOf(name) === 0) {
        return c.substring(name.length, c.length);
      }
    }
    return null;
  }
}
