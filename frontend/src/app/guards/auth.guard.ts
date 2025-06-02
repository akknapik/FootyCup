import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Observable, of } from 'rxjs';
import { filter, map, take, switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): Observable<boolean> {
  const isLoginRoute = this.router.url.startsWith('/login');
  if (isLoginRoute) {
    return of(true);
  }

  return this.auth.currentUser$.pipe(
    switchMap(user => {
      if (user) {
        return of(true);
      }
      return this.auth.loadCurrentUser().pipe(
        map(loadedUser => !!loadedUser)
      );
    }),
    take(1)
  );
}
}
