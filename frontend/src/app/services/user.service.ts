import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = '/api/users'; 
  
  constructor(private http: HttpClient) { }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl, { withCredentials: true });
  }

  getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`, { withCredentials: true });
  }

  createUser(user: Partial<User>): Observable<User> {
    return this.http.post<User>(this.apiUrl, user, { withCredentials: true });
  }

  updateUser(id: number, user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user, { withCredentials: true });
  }

  updateProfile(payload: { firstname: string; lastname: string }): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/me/profile`, payload, { withCredentials: true });
  }

  changePassword(payload: { currentPassword: string; newPassword: string; confirmPassword: string }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/me/password`, payload, { withCredentials: true });
  }

  deleteAccount(payload: { password: string }): Observable<void> {
    return this.http.request<void>('delete', `${this.apiUrl}/me`, {
      body: payload,
      withCredentials: true
    });
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}
