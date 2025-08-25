import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Observable, of } from 'rxjs';
import { filter, map, take, switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    if (state.url.startsWith('/login')) {
          return of(true);
        }

    return this.auth.currentUser$.pipe(
      switchMap(user => {
        if (user) {
          return of(true);
        }
        return this.auth.loadCurrentUser().pipe(
          map(loadedUser => loadedUser
            ? true
            : this.router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url }})
          )
        );
      }),
      take(1)
    );
  }
}
