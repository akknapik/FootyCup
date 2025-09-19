import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TournamentDetailsComponent } from './tournament-details.component';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { User } from '../../../models/user.model';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { TournamentService } from '../../../services/tournament.service';

describe('TournamentDetailsComponent', () => {
  let component: TournamentDetailsComponent;
  let fixture: ComponentFixture<TournamentDetailsComponent>;
  let tournamentServiceSpy: jasmine.SpyObj<TournamentService>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;

  const mockTournament = {
    id: 1,
    name: 'Test Tournament',
    location: 'Test City'
  };

  const mockReferees: User[] = [
    { id: 1, firstname: 'John', lastname: 'Doe', email: 'john@example.com', userRole: 'USER' }
  ];

  beforeEach(async () => {
    tournamentServiceSpy = jasmine.createSpyObj(
      'TournamentService',
      ['getTournamentById', 'updateTournament', 'getReferees', 'addReferee'],
      { tournamentId$: of('1') }
    );
    tournamentServiceSpy.getTournamentById.and.returnValue(of(mockTournament));
    tournamentServiceSpy.getReferees.and.returnValue(of(mockReferees));
    tournamentServiceSpy.addReferee.and.returnValue(of(mockReferees));
    tournamentServiceSpy.updateTournament.and.returnValue(of({}));

    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['showError', 'showSuccess']);

    await TestBed.configureTestingModule({
      declarations: [TournamentDetailsComponent],
      imports: [FormsModule, RouterTestingModule],
      providers: [
        { provide: TournamentService, useValue: tournamentServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (key: string) => key === 'tournamentId' ? '1' : null
              }
            }
          }
        },
        { provide: AuthService, useValue: { currentUser$: of({ firstname: 'Jane', lastname: 'Smith' }) } },
        { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TournamentDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update referees when onAddReferee is called', () => {
    const updatedReferees: User[] = [
      ...mockReferees,
      { id: 2, firstname: 'Alice', lastname: 'Smith', email: 'alice@example.com', userRole: 'USER' }
    ];

    tournamentServiceSpy.addReferee.and.returnValue(of(updatedReferees));

    component.onAddReferee(' alice@example.com ');

    expect(tournamentServiceSpy.addReferee).toHaveBeenCalledWith(1, 'alice@example.com');
    expect(component.referees).toEqual(updatedReferees);
    expect(notificationServiceSpy.showSuccess).toHaveBeenCalled();
    expect(component.newRefereeEmail).toBe('');
  });
});