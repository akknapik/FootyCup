import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchComponent } from './match.component';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../../services/auth.service';
import { FormatService } from '../../services/format.service';
import { MatchService } from '../../services/match.service';
import { NotificationService } from '../../services/notification.service';
import { TournamentService } from '../../services/tournament.service';

describe('MatchComponent', () => {
  let component: MatchComponent;
  let fixture: ComponentFixture<MatchComponent>;

  beforeEach(async () => {
    const matchServiceMock = jasmine.createSpyObj('MatchService', ['getMatches', 'deleteMatch', 'generateGroupMatches', 'assignReferee']);
    matchServiceMock.getMatches.and.returnValue(of([]));
    matchServiceMock.deleteMatch.and.returnValue(of(void 0));
    matchServiceMock.generateGroupMatches.and.returnValue(of(void 0));
    matchServiceMock.assignReferee.and.returnValue(of({}));

    const notificationServiceMock = jasmine.createSpyObj('NotificationService', ['showError', 'showSuccess', 'confirm']);
    notificationServiceMock.confirm.and.returnValue(of(false));

    const formatServiceMock = jasmine.createSpyObj('FormatService', ['getGroups']);
    formatServiceMock.getGroups.and.returnValue(of([]));

    const tournamentServiceMock = jasmine.createSpyObj('TournamentService', ['getReferees']);
    tournamentServiceMock.getReferees.and.returnValue(of([]));

    const authServiceMock: any = {
      currentUser$: of(null),
      logout: jasmine.createSpy('logout').and.returnValue(of(null))
    };
    Object.defineProperty(authServiceMock, 'currentUser', { get: () => null });
    await TestBed.configureTestingModule({
  declarations: [MatchComponent],
        imports: [FormsModule, RouterTestingModule],
        providers: [
          { provide: MatchService, useValue: matchServiceMock },
          { provide: NotificationService, useValue: notificationServiceMock },
          { provide: FormatService, useValue: formatServiceMock },
          { provide: TournamentService, useValue: tournamentServiceMock },
          { provide: AuthService, useValue: authServiceMock },
          { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } }
        ]
      }).compileComponents();

    fixture = TestBed.createComponent(MatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
