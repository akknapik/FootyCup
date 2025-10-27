import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { NotificationService } from '../services/notification.service';

@Injectable({ providedIn: 'root' })
export class ErrorService implements HttpInterceptor {

  constructor(private router: Router, private notification: NotificationService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        const url = req.url || '';
        const isMeEndpoint   = url.includes('/api/users/me');
        const isLoginCall    = url.includes('/api/auth/login');
        const onLoginPage    = this.router.url.startsWith('/login');

        if (error.status === 0) {
          this.notification.showError('Network error. Check server or CORS/proxy.');
          return throwError(() => error);
        }

        if (error.status === 401 || error.status === 403) {
          if (!onLoginPage && !isLoginCall && !isMeEndpoint) {
            const returnUrl = this.router.url;
            this.router.navigate(['/login'], { queryParams: { returnUrl }});
          }
          return throwError(() => error);
        }

        let errorMsg = 'An error has occurred';
        try {
          const parsed = typeof error.error === 'string' ? JSON.parse(error.error) : error.error;
          errorMsg = parsed?.message || errorMsg;
        } catch {}
        this.notification.showError(errorMsg);

        return throwError(() => error);
      })
    );
  }
}
