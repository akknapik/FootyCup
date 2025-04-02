import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  register(data: any) {
    return this.http.post('/api/register', data, { responseType: 'text' });
  }
  

  login(data: {email: string, password: string}) {
    return this.http.post<{token: string }>('/api/login', data).pipe(
      tap((response) => {
        localStorage.setItem('token', response.token);
      })
    );
  }

  getToken() {
    return localStorage.getItem('token');
  }

  logout() {
    localStorage.removeItem('token');
  }
}
