import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private snackBar: MatSnackBar) {}

  showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      panelClass: ['snackbar-error'],
      verticalPosition: 'top',
    });
  }

  showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      panelClass: ['snackbar-success'],
      verticalPosition: 'top',
    });
  }

  confirm(message: string): Observable<boolean> {
    const subject = new Subject<boolean>();
    const ref = this.snackBar.open(message, 'Tak', {
      duration: 5000,
      panelClass: ['snackbar-confirm'],
      verticalPosition: 'top',
    });

    ref.onAction().subscribe(() => {
      subject.next(true);
      subject.complete();
    });

    setTimeout(() => {
      subject.next(false);
      subject.complete();
    }, 5000);

    return subject.asObservable();
  }
}
