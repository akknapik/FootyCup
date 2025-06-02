import { Injectable } from '@angular/core';
import {
  HttpInterceptor, HttpRequest, HttpHandler,
  HttpEvent, HttpErrorResponse
} from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { NotificationService } from '../services/notification.service';

@Injectable({
  providedIn: 'root'
})
export class ErrorService implements HttpInterceptor {

  constructor(
    private router: Router,
    private notification: NotificationService
  ) {}

intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  return next.handle(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMsg = 'An error has occurred';

      try {
        const parsed = typeof error.error === 'string'
          ? JSON.parse(error.error)
          : error.error;

        errorMsg = parsed?.message || errorMsg;
      } catch {
      }

      this.notification.showError(errorMsg);

      if (error.status === 401 || error.status === 403) {
        this.router.navigate(['/logout']); 
      }

      return throwError(() => error);
    })
  );
}
}
