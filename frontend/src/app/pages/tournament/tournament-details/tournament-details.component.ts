import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TournamentService } from '../../../services/tournament.service';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { User } from '../../../models/user.model';
import { TournamentResponse } from '../../../models/tournament/tournament.response';
import { UserRef } from '../../../models/common/user-ref.model';
import { HttpResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-tournament-details',
  standalone: false,
  templateUrl: './tournament-details.component.html',
  styleUrls: ['./tournament-details.component.css']
})
export class TournamentDetailsComponent implements OnInit {
  tournamentId!: number;
  tournament!: TournamentResponse;
  referees: UserRef[] = [];
  newRefereeEmail = '';
  currentUser: User | null = null;
  isProcessingFollow = false;
  isQrModalOpen = false;
  isGeneratingQr = false;
  isLoadingQr = false;
  qrCodeDataUrl: string | null = null;
  isExportingTournament: Record<'pdf' | 'csv', boolean> = { pdf: false, csv: false };

  constructor(
    private route: ActivatedRoute,
    private tournamentService: TournamentService,
    private router: Router,
    public auth: AuthService,
    private notification: NotificationService
  ) {
    this.tournamentService.tournamentId$.subscribe((id) => {
      if (id) {
        this.tournamentId = +id;
        this.loadTournamentDetails(this.tournamentId);
      }
    }
    );
    this.auth.currentUser$.subscribe(user => this.currentUser = user);
  }

  ngOnInit(): void {
    this.tournamentId = +this.route.snapshot.paramMap.get('tournamentId')!;
    this.loadTournamentDetails(this.tournamentId);
  }

  goToTeams(): void {
    this.router.navigate(['/tournament', this.tournamentId, 'teams']);
  }

  goBack(): void {
    if (this.auth.isLoggedIn()) {
      this.router.navigate(['/tournaments/my']);
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  updateTournament() {
    if (!this.canManageTournament) {
      return;
    }
    const payload = {
      name: this.tournament.name,
      location: this.tournament.location,
      publicVisible: this.tournament.publicVisible
      };

    this.tournamentService.updateTournament(this.tournamentId, payload).subscribe({
      next: () => {
        this.notification.showSuccess('Tournament updated successfully!');
      },
      error: () => this.notification.showError('Error updating tournament')
    });
  }

  onAddReferee(email: string): void {
    if (!this.canManageTournament) {
      return;
    }
    const trimmedEmail = email.trim();

    if (!trimmedEmail) {
      this.notification.showError('Please provide referee email');
      return;
    }

    this.tournamentService.addReferee(this.tournamentId, trimmedEmail).subscribe({
      next: (referees) => {
        this.referees = referees;
        this.notification.showSuccess('Referee added successfully');
        this.newRefereeEmail = '';
      },
      error: () => this.notification.showError('Error adding referee')
    });
  }

  private loadTournamentDetails(id: number): void {
    this.isQrModalOpen = false;
    this.qrCodeDataUrl = null;
    this.isLoadingQr = false;
    this.isGeneratingQr = false;

    const request$ = this.auth.isLoggedIn()
      ? this.tournamentService.getTournamentById(id)
      : this.tournamentService.getPublicTournamentById(id);

    request$.subscribe({
      next: (data) => {
        this.tournament = data;
        this.referees = data.referees ?? [];
        if (this.canManageTournament) {
          this.loadReferees(id);
        }
      },
      error: () => this.notification.showError('Error loading tournament details')
    });
  }

  private loadReferees(id: number): void {
    this.tournamentService.getReferees(id).subscribe({
      next: (data) => this.referees = data,
      error: () => this.notification.showError('Error loading referees')
    });
  }

  removeReferee(userId: number) {
    if (!this.canManageTournament) {
      return;
    }
    this.tournamentService.removeReferee(this.tournamentId, userId).subscribe({
      next: () => {
        this.notification.showSuccess('Referee removed');
        this.loadReferees(this.tournamentId);
        this.loadTournamentDetails(this.tournamentId);
      },
      error: () => this.notification.showError('Error removing referee')
    });
  }

  logout(): void {
    this.auth.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }

  private isAdminUser(): boolean {
    return this.currentUser?.userRole === 'ADMIN';
  }
  
  get canManageTournament(): boolean {
    if (!this.currentUser || !this.tournament) {
      return false;
    }
    if (this.isAdminUser()) {
      return true;
    }
    return this.tournament.organizer?.id === this.currentUser.id;
  }

  get canFollowTournament(): boolean {
    return !!this.currentUser;
  }

  toggleFollow(): void {
    if (!this.canFollowTournament || !this.tournament) {
      this.notification.showInfo('Log in to observe tournaments.');
      return;
    }

    this.isProcessingFollow = true;
    const request$ = this.tournament.followed
      ? this.tournamentService.unfollowTournament(this.tournamentId)
      : this.tournamentService.followTournament(this.tournamentId);

    request$.subscribe({
      next: () => {
        this.tournament.followed = !this.tournament.followed;
        this.notification.showSuccess(this.tournament.followed ? 'You are now observing this tournament.' : 'You stopped observing this tournament.');
      },
      error: () => {
        this.notification.showError('Could not update observation status');
        this.isProcessingFollow = false;
      },
      complete: () => this.isProcessingFollow = false
    });
  }

  generateQrCode(): void {
    if (!this.canManageTournament || this.isGeneratingQr) {
      return;
    }

    this.isGeneratingQr = true;
    this.tournamentService.generateTournamentQrCode(this.tournamentId).subscribe({
      next: (response) => {
        if (this.tournament) {
          this.tournament.qrCodeGenerated = response.generated;
        }
        this.qrCodeDataUrl = this.buildQrCodeDataUrl(response.imageBase64);
        this.isQrModalOpen = true;
        this.notification.showSuccess('QR code generated successfully.');
      },
      error: () => {
        this.notification.showError('Error generating QR code');
        this.isGeneratingQr = false;
      },
      complete: () => this.isGeneratingQr = false
    });
  }

  openQrModal(): void {
    if (!this.tournament?.qrCodeGenerated) {
      return;
    }

    if (this.qrCodeDataUrl) {
      this.isQrModalOpen = true;
      return;
    }

    this.isLoadingQr = true;
    this.tournamentService.getTournamentQrCode(this.tournamentId).subscribe({
      next: (response) => {
        this.qrCodeDataUrl = this.buildQrCodeDataUrl(response.imageBase64);
        this.isQrModalOpen = true;
      },
      error: () => {
        this.notification.showError('Error loading QR code');
        this.isLoadingQr = false;
      },
      complete: () => this.isLoadingQr = false
    });
  }

  closeQrModal(): void {
    this.isQrModalOpen = false;
  }

  downloadQrCode(): void {
    if (!this.tournament?.qrCodeGenerated) {
      return;
    }

    this.tournamentService.downloadTournamentQrCode(this.tournamentId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `tournament-${this.tournamentId}-qr.png`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      },
      error: () => this.notification.showError('Error downloading QR code')
    });
  }

  exportTournament(format: 'pdf' | 'csv'): void {
    if (this.isExportingTournament[format]) {
      return;
    }

    this.isExportingTournament[format] = true;
    this.tournamentService.exportTournament(this.tournamentId, format)
      .pipe(finalize(() => this.isExportingTournament[format] = false))
      .subscribe({
        next: (response) => this.handleFileDownload(response, `tournament-${this.tournamentId}.${format}`),
        error: () => this.notification.showError('Failed to export tournament data.')
      });
  }

  private handleFileDownload(response: HttpResponse<Blob>, fallbackName: string): void {
    const blob = response.body;
    if (!blob) {
      this.notification.showError('The export response did not contain any data.');
      return;
    }

    const filename = this.extractFilename(response.headers.get('content-disposition')) ?? fallbackName;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }

  private extractFilename(header: string | null): string | null {
    if (!header) {
      return null;
    }
    const match = /filename\*=UTF-8''([^;]+)|filename="?([^";]+)"?/i.exec(header);
    if (match) {
      return decodeURIComponent(match[1] || match[2]);
    }
    return null;
  }

  private buildQrCodeDataUrl(imageBase64: string): string {
    return `data:image/png;base64,${imageBase64}`;
  }
}
